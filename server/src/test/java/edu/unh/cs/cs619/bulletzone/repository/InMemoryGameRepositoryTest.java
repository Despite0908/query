package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class InMemoryGameRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
    }

    @Test
    public void testJoin() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
    }

    @Test
    public void join_InjectTankSpawn_TankSpawnsAt12x12() {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    //Basic Turns
    @Test
    public void turn_turnRight_returnsTrueAndTankRight() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);
    }

    @Test
    public void turn_turnLeft_returnsTrueAndTankLeft() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }

    @Test
    public void turn_turnUp_returnsTrueAndTankUp() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    //Check multiple turns
    @Test
    public void turn_turnDownFromLeft_returnsTrueAndTankDown() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        TimeUnit.MILLISECONDS.sleep(500);

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Down);
    }

    //Error and constraint checking for turn
    @Test
    public void turn_tankDoesNotExist_ThrowsError() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        thrown.expect(TankDoesNotExistException.class);
        thrown.expectMessage("Tank '1000' does not exist");
        repo.turn(1000, Direction.Right);
    }

    @Test
    public void turn_DownFromUp_returnsFalseAndTankUp() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    @Test
    public void turn_RightFromLeft_returnsFalseAndTankLeft() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        TimeUnit.MILLISECONDS.sleep(500);

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }

    @Test
    public void turn_LeftFromRight_returnsFalseAndTankRight() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Right);
    }

    @Test
    public void turn_UpFromDown_returnsFalseAndTankDown() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Down);

        TimeUnit.MILLISECONDS.sleep(500);

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Down);
    }

    //TODO: This is a bad way to test this, do better
    @Test
    public void turn_TwoTurnsNoWait_returnsFalseAndTankLeft() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }



    @Test
    public void testMove() throws Exception {

    }

    @Test
    public void testFire() throws Exception {

    }

    @Test
    public void testLeave() throws Exception {

    }
}