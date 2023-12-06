package edu.unh.cs.cs619.bulletzone.repository;

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
        repo.setBuilderSpawn(6, 6);
        repo.injectClock(mockClock);
    }

    @Test
    public void move_validMoveBuilderUp_return1() throws Exception{
        Builder builder = repo.join("", -1).getBuilder();
        Assert.assertNotNull(builder);
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.move(builder.getId(), Direction.Up), 1);
    }

    @Test
    public void turn_multipleLeftTurns_return1() throws Exception{
        Builder builder = repo.join("", -1).getBuilder();
        Assert.assertNotNull(builder);
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);

        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Down), true);

        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Left), true);
    }

    @Test
    public void turn_RLR_return1() throws Exception{
        Builder builder = repo.join("", -1).getBuilder();
        Assert.assertNotNull(builder);
        when(mockClock.millis()).thenReturn((long)500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);

        when(mockClock.millis()).thenReturn((long)1000);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Up), true);

        when(mockClock.millis()).thenReturn((long)1500);
        Assert.assertEquals(repo.turn(builder.getId(), Direction.Right), true);
    }
}