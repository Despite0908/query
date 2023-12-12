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
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class DeckTest {

    @InjectMocks
    InMemoryGameRepository repo;

    @Mock
    Clock mockClock;
    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("WaterTestMap.json");
        repo.setTankSpawn(3, 0);
        repo.setBuilderSpawn(0, 0);
        repo.injectClock(mockClock);
    }

    @Test
    public void move_builderOntoDockNormalStep_return1() throws Exception {
        when(mockClock.millis()).thenReturn((long)250);
        Builder builder = repo.join("", -1).getBuilder();
        repo.turn(builder.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)500);
        repo.turn(builder.getId(), Direction.Down);
        when(mockClock.millis()).thenReturn((long)750);
        Assert.assertEquals(1, repo.move(builder.getId(), Direction.Down));
    }

    @Test
    public void move_tankOntoDockNormalStep_return1() throws Exception {
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();

        //move up twice
        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
    }

    @Test
    public void move_soldierOntoDockNormalStep_return1() throws Exception {
        when(mockClock.millis()).thenReturn((long)1000);
        Tank tank = repo.join("", -1).getTank();
        Soldier soldier = repo.eject(tank.getId());

        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(1, repo.move(soldier.getId(), Direction.Up));
    }

}