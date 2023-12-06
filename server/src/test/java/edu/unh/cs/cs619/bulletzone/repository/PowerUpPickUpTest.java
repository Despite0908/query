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

}
