package edu.unh.cs.cs619.bulletzone.repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.improvements.Deck;
import edu.unh.cs.cs619.bulletzone.model.improvements.Road;
import edu.unh.cs.cs619.bulletzone.model.improvements.Wall;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class BuilderOperationsTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        repo.setBuilderSpawn(0, 0);
        repo.injectClock(mockClock);
    }

    //turn tests
    @Test
    public void move_validMoveBuilderUp_returnTrue() throws Exception{
        Builder builder = repo.join("", 1).getBuilder();
        Assert.assertNotNull(builder);
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
    }
    @Test
    public void turn_multipleLeftTurns_returnTrue() throws Exception{
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);

        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Down), true);

        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Left), true);
    }

    @Test
    public void turn_RLR_returnTrue() throws Exception{
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);

        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Up), true);

        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);
    }

    //move tests
    @Test
    public void move_movesAtNormalStep_returns1() throws Exception{
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)250);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);

        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);

        when(mockClock.millis()).thenReturn((long)750);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
    }

    @Test
    public void move_movesAt100msStep_returns0() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)250);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);

        when(mockClock.millis()).thenReturn((long)350);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 0);
    }

    @Test
    public void move_sideways_returns0() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)250);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Right), 0);
    }


    //fire tests
    @Test
    public void fire_fireAtNormalStep_returnsTrue() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)500);
        assertTrue(repo.fire(builder.getId(), 1));

        when(mockClock.millis()).thenReturn((long)1000);
        assertTrue(repo.fire(builder.getId(), 1));

        when(mockClock.millis()).thenReturn((long)1500);
        assertTrue(repo.fire(builder.getId(), 1));
    }

    @Test
    public void fire_fireAtMoveStep_returnsFalse() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)500);
        assertTrue(repo.fire(builder.getId(), 1));

        when(mockClock.millis()).thenReturn((long)750);
        assertFalse(repo.fire(builder.getId(), 1));
    }

    @Test
    public void fire_fireMoreThan4_returnsFalse() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        when(mockClock.millis()).thenReturn((long)500);
        assertTrue(repo.fire(builder.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);

        when(mockClock.millis()).thenReturn((long)1000);
        assertTrue(repo.fire(builder.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);

        when(mockClock.millis()).thenReturn((long)1500);
        assertTrue(repo.fire(builder.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);

        when(mockClock.millis()).thenReturn((long)2000);
        assertTrue(repo.fire(builder.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);

        when(mockClock.millis()).thenReturn((long)2500);
        assertFalse(repo.fire(builder.getId(), 1));
    }

    //build tests

    @Test
    public void build_buildWall_wallBuilt() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, true));
        assertThat(builder.getParent().getNeighbor(Direction.Down).getImprovement(), instanceOf(Wall.class));
    }

    @Test
    public void build_buildRoad_roadBuilt() throws Exception {
        BankLinker.addCredits(1, 80);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 0, true));
        assertThat(builder.getParent().getNeighbor(Direction.Down).getImprovement(), instanceOf(Road.class));
    }

    @Test
    public void build_buildDockOnWater_dockBuilt() throws Exception {
        BankLinker.addCredits(1, 40);
        repo.setMapPath("WaterBox.json");
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 2, true));
        assertThat(builder.getParent().getNeighbor(Direction.Down).getImprovement(), instanceOf(Deck.class));
    }

    @Test
    public void build_buildDockOnLand_returnsFalse() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertFalse(repo.build(builder.getId(), (byte) 2, true));
    }

    @Test
    public void build_buildWallOrRoadWater_returnsFalse() throws Exception {
        repo.setMapPath("WaterBox.json");
        Builder builder = repo.join("", 1).getBuilder();
        assertFalse(repo.build(builder.getId(), (byte) 0, true));
        assertFalse(repo.build(builder.getId(), (byte) 1, true));
    }

    @Test
    public void build_MoveOrTurnWhileBuilding_returnsFalse() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, false));
        assertEquals(repo.move(builder.getId(), Direction.Up), 0);
        assertFalse(repo.turn(builder.getId(), Direction.Right));
    }

    @Test
    public void build_FireWhileBuilding_returnsTrue() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, false));
        assertTrue(repo.fire(builder.getId(), 1));
    }

    //dismantle tests

    @Test
    public void dismantle_normalDismantle_noImprovement() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, true));
        assertTrue(repo.dismantle(builder.getId(), true));
        assertFalse(builder.getParent().getNeighbor(Direction.Down).isImproved());
    }

    @Test
    public void dismantle_noImprovement_returnsFalse() throws Exception {
        Builder builder = repo.join("", 1).getBuilder();
        assertFalse(repo.dismantle(builder.getId(), true));
    }

    @Test
    public void dismantle_DismantleWhileBuilding_ReturnsFalse() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, false));
        assertFalse(repo.dismantle(builder.getId(), true));
    }

    @Test
    public void dismantle_moveOrTurnWhileDismantle_cancelAndExecute() throws Exception {
        BankLinker.addCredits(1, 100);
        Builder builder = repo.join("", 1).getBuilder();
        assertTrue(repo.build(builder.getId(), (byte) 1, true));
        assertTrue(repo.dismantle(builder.getId(), false));
        when(mockClock.millis()).thenReturn((long)500);
        assertEquals(repo.move(builder.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)1000);
        assertTrue(repo.turn(builder.getId(), Direction.Right));
    }

}