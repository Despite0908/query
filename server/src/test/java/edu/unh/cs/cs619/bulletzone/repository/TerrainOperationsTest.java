package edu.unh.cs.cs619.bulletzone.repository;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TerrainOperationsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Mock
    Clock mockClock;
    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("TerrainTestMap.json");
        repo.setTankSpawn(15, 0);
        repo.injectClock(mockClock);
    }

    @Test
    public void move_hillsMoveNormalTankStep_returnFalse() throws Exception {
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 0);
    }
    @Test
    public void move_hillsMoveDelayTankStep_returnTrue() throws Exception {
        when(mockClock.millis()).thenReturn((long)750);
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
    }
    @Test
    public void move_hillsMoveNormalSoldierStep_returnTrue() throws Exception {
        when(mockClock.millis()).thenReturn((long)1000);
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)2000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
    }

    @Test
    public void move_rockyMoveNormalSoliderStep_returnFalse() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        when(mockClock.millis()).thenReturn((long)1000);
        repo.turn(soldier.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)2000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Right), 1);
        //turn instantly, don't step as we want to go from 1 move to another
        when(mockClock.millis()).thenReturn((long)2000);
        repo.turn(soldier.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)3000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 0);
    }
    @Test
    public void move_rockyMoveDelaySoldierStep_returnTrue() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        when(mockClock.millis()).thenReturn((long)1000);
        repo.turn(soldier.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)2000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Right), 1);
        //turn instantly, don't step as we want to go from 1 move to another
        when(mockClock.millis()).thenReturn((long)2000);
        repo.turn(soldier.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)3500);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
    }
    @Test
    public void move_rockyMoveNormalTankStep_returnTrue() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        when(mockClock.millis()).thenReturn((long)500);
        repo.turn(tank.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 1);
        when(mockClock.millis()).thenReturn((long)1500);
        repo.turn(tank.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)2000);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
    }

    @Test
    public void move_forestTank_returnsFalse() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        when(mockClock.millis()).thenReturn((long)500);
        repo.turn(tank.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 1);
        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 1);
        when(mockClock.millis()).thenReturn((long)2000);
        repo.turn(tank.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)2500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 0);
    }

    @Test
    public void move_forestSoldier_returnsTrue() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        when(mockClock.millis()).thenReturn((long)1000);
        repo.turn(soldier.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)2000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Right), 1);
        when(mockClock.millis()).thenReturn((long)3000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Right), 1);
        //turn instantly, don't step as we want to go from 1 move to another
        when(mockClock.millis()).thenReturn((long)3000);
        repo.turn(soldier.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)4000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
    }
}