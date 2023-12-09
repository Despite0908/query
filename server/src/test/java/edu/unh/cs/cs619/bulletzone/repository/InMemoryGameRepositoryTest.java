package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class InMemoryGameRepositoryTest {
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        repo.setBuilderSpawn(6, 6);
    }

    @Test
    public void join_normalJoin_returnTrue() throws Exception {
        Player p = repo.join("", -1);
        Tank tank = p.getTank();
        Builder b = p.getBuilder();
        Assert.assertNotNull(tank);
        Assert.assertNotNull(b);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
    }

    @Test
    public void join_InjectTankSpawn_TankSpawnsAt12x12() {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void leave_tankDoesNotExist_Throws(){
        repo.join("", -1).getTank();
        Assert.assertThrows(TokenDoesNotExistException.class, () -> repo.leave(2));
    }

    @Test
    public void leave_tankExists_doesNotThrow(){
        Tank tank = repo.join("", -1).getTank();
        try {
            repo.leave(tank.getId());
        } catch (TokenDoesNotExistException e) {
            Assert.fail();
        }
    }

    //Basic Turns
    @Test
    public void turn_turnRight_returnsTrueAndTankRight() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);
    }

    @Test
    public void turn_turnLeft_returnsTrueAndTankLeft() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }

    @Test
    public void turn_turnUp_returnsTrueAndTankUp() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    //Check multiple turns
    @Test
    public void turn_turnDownFromLeft_returnsTrueAndTankDown() throws Exception {
        Tank tank = repo.join("", -1).getTank();
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
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertFalse(repo.turn(1000, Direction.Right));
    }

    @Test
    public void turn_DownFromUp_returnsFalseAndTankUp() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    @Test
    public void turn_RightFromLeft_returnsFalseAndTankLeft() throws Exception {
        Tank tank = repo.join("", -1).getTank();
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
        Tank tank = repo.join("", -1).getTank();
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
        Tank tank = repo.join("", -1).getTank();
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

    @Test
    public void turn_TwoTurnsNoWait_returnsFalseAndTankLeft() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }


    //Test Valid Moves
    @Test
    public void move_ValidMoveTankUp_ReturnsTrueTankMoves() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[11][12]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Down), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[11][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankDown_ReturnsTrueTankMoves() throws Exception {
        Tank tank = repo.join("", -1).getTank();

        repo.turn(tank.getId(), Direction.Left);
        TimeUnit.MILLISECONDS.sleep(500);
        repo.turn(tank.getId(), Direction.Down);
        Assert.assertTrue(tank.getDirection() == Direction.Down);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[11][12]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Down), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[11][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankRight_ReturnsTrueTankMoves() throws Exception {
        Tank tank = repo.join("", -1).getTank();

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][13]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Left), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][13]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankLeft_ReturnsTrueTankMoves() throws Exception {
        Tank tank = repo.join("", -1).getTank();

        repo.turn(tank.getId(), Direction.Left);
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][13]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Left), 1);
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][13]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    //Test Breaking Constraints
    @Test
    public void move_SidewaysMoveTankUp_ReturnsTrueFalse() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 0);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Left), 0);
    }

    @Test
    public void move_SidewaysMoveTankRight_ReturnsFalse() throws Exception {
        Tank tank = repo.join("", -1).getTank();

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertSame(tank.getDirection(), Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 0);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Down), 0);
    }

    @Test
    public void move_ConsecutiveMoves_ReturnsFalse() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertSame(tank.getDirection(), Direction.Up);

        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Down), 0);
    }

    //Test Hitting Walls
    @Test
    public void move_tankHitsWall_returnsFalse() throws Exception{
        repo.setMapPath("BoxedIn.json");
        repo.setTankSpawn(0, 0);
        Tank tank = repo.join("", -1).getTank();

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertSame(tank.getDirection(), Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Right), 0);
    }

    @Test
    public void fire_bulletFired_returnsTrue() throws Exception {;
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_consecutiveBulletsFired_returnsFalse() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_thirdBulletFired_returnsFalse() throws Exception {
        Tank tank = repo.join("", -1).getTank();
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }
}