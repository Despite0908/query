package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.ItemTypes;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class PowerUpCreateTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
    }

    /**
     * addAntiGravTest -- tests that an anti grav powerup can be added
     * @throws Exception
     */
    @Test
    public void addAntiGravTest() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        AtomicLong idGenerator = new AtomicLong();
        Item myTestItem = new Item(idGenerator.getAndIncrement(), ItemTypes.ANTI_GRAV, 188);
        Assert.assertNotNull(myTestItem);
    }

    /**
     * addFusionReactorTest -- tests that a fusion reactor powerup can be added
     * @throws Exception
     */
    @Test
    public void addFusionReactorTest() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        AtomicLong idGenerator = new AtomicLong();
        Item myTestItem = new Item(idGenerator.getAndIncrement(), ItemTypes.FUSION_REACTOR, 188);
        Assert.assertNotNull(myTestItem);
    }

    /**
     * addThingamagigTest -- tests that a thingamagig powerup can be added
     * @throws Exception
     */
    @Test
    public void addThingamagigTest() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        AtomicLong idGenerator = new AtomicLong();
        Item myTestItem = new Item(idGenerator.getAndIncrement(), ItemTypes.COIN, 188);
        Assert.assertNotNull(myTestItem);
    }

}
