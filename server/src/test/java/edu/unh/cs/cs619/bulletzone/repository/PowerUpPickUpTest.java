package edu.unh.cs.cs619.bulletzone.repository;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.ItemTypes;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PowerUpPickUpTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("PowerUpTestMap.json");
        repo.setTankSpawn(15, 0);
        repo.injectClock(mockClock);
    }



    /**
     * pickupAntiGravTestMovementModifierTest -- tests that the
     * movement speed is correct after getting an AntiGrav PowerUp.
     * @throws Exception
     */
    @Test
    public void pickupAntiGravTestMovementModifierTest() throws Exception {
        //when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();
        Assert.assertNotNull(tank);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        long newMovementInterval = tank.getAllowedMoveInterval();
        Assert.assertEquals(250, newMovementInterval);

    }

    /**
     * pickupAntiGravTestFiringModifierTest -- tests that the
     * firing interval is correct after getting an AntiGrav PowerUp.
     * @throws Exception
     */
    @Test
    public void pickupAntiGravTestFiringModifierTest() throws Exception {
        //when(mockClock.millis()).thenReturn((long)1000);
        Tank tank = repo.join("", -1).getTank();
        Assert.assertNotNull(tank);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        long newFiringInterval = tank.getAllowedFireInterval();
        Assert.assertEquals(600, newFiringInterval);
    }

    /**
     * pickupAntiGravTestMovementModifierTest -- tests that the
     * movement speed is correct after getting an AntiGrav PowerUp.
     * @throws Exception
     */
    @Test
    public void pickupMultipleAntiGravTestMovementModifierTest() throws Exception {
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();
        Assert.assertNotNull(tank);
        long thisMillis = mockClock.millis();
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
        when(mockClock.millis()).thenReturn((long)1500);
        repo.turn(tank.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)2500);
        long thisMillis2 = mockClock.millis();
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Right));
        when(mockClock.millis()).thenReturn((long)3500);
        repo.turn(tank.getId(), Direction.Up);
        when(mockClock.millis()).thenReturn((long)4500);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
        when(mockClock.millis()).thenReturn((long)5500);
        repo.turn(tank.getId(), Direction.Left);
        when(mockClock.millis()).thenReturn((long)6500);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Left));
        long newMovementInterval = tank.getAllowedMoveInterval();
        Assert.assertEquals(195, newMovementInterval);
    }

    /**
     * pickupAntiGravTestMovementModifierTest -- tests that the
     * movement speed is correct after getting an AntiGrav PowerUp.
     * @throws Exception
     */
    @Test
    public void pickupMultipleAntiGravTestMovementModifierTest2() throws Exception {
        when(mockClock.millis()).thenReturn((long)500);
        Tank tank = repo.join("", -1).getTank();
        Assert.assertNotNull(tank);
        long thisMillis = mockClock.millis();
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
        when(mockClock.millis()).thenReturn((long)1500);
        long thisMillis2 = mockClock.millis();
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Up));
        when(mockClock.millis()).thenReturn((long)3500);
        repo.turn(tank.getId(), Direction.Right);
        when(mockClock.millis()).thenReturn((long)4500);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Right));
        when(mockClock.millis()).thenReturn((long)5500);
        repo.turn(tank.getId(), Direction.Down);
        when(mockClock.millis()).thenReturn((long)6500);
        Assert.assertEquals(1, repo.move(tank.getId(), Direction.Down));
        long newMovementInterval = tank.getAllowedMoveInterval();
        Assert.assertEquals(195, newMovementInterval);
    }

}
