package edu.unh.cs.cs619.bulletzone.model.improvements;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class RoadTest {

    @InjectMocks
    InMemoryGameRepository repo;

    @Mock
    Clock mockClock;
    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(15, 15);
        repo.setBuilderSpawn(0, 0);
        repo.injectClock(mockClock);
    }

    @Test
    public void move_tankMovesTwiceFaster_returns1() throws Exception{
        when(mockClock.millis()).thenReturn((long)250);
        Tank tank = repo.join("", -1).getTank();
        FieldHolder next = tank.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
    }

    @Test
    public void move_tankMovesNormalThen100ms_returns1Then0() throws Exception{
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();
        FieldHolder next = tank.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)600);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 0);
    }

    @Test
    public void move_builderMovesTwiceFaster_returns1() throws Exception{
        when(mockClock.millis()).thenReturn((long)125);
        Builder builder = repo.join("", -1).getBuilder();
        FieldHolder next = builder.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)250);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
    }

    @Test
    public void move_builderMovesNormalThen100ms_returns1Then0() throws Exception{
        when(mockClock.millis()).thenReturn((long)250);
        Builder builder = repo.join("", -1).getBuilder();
        FieldHolder next = builder.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)350);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 0);
    }

    @Test
    public void move_soldierMovesTwiceFaster_returns1() throws Exception{
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();
        Soldier soldier = repo.eject(tank.getId());
        FieldHolder next = soldier.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
    }

    @Test
    public void move_soldierMovesNormalThen100ms_returns1Then0() throws Exception{
        when(mockClock.millis()).thenReturn((long)1000);
        Tank tank = repo.join("", -1).getTank();
        Soldier soldier = repo.eject(tank.getId());
        FieldHolder next = soldier.getParent().getNeighbor(Direction.Up);
        next.setImprovement(new Road());
        next.getNeighbor(Direction.Up).setImprovement(new Road());
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 1);
        when(mockClock.millis()).thenReturn((long)1100);
        Assert.assertEquals(repo.move(soldier.getId(), Direction.Up), 0);
    }

}