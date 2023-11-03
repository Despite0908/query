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
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;

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
    public void join_normalJoin_returnTrue() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
        int[][] grid = repo.getGrid();
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

    @Test
    public void leave_tankDoesNotExist_Throws(){
        repo.join("");
        Assert.assertThrows(TokenDoesNotExistException.class, () -> repo.leave(2));
    }

    @Test
    public void leave_tankExists_doesNotThrow(){
        Tank tank = repo.join("");
        try {
            repo.leave(tank.getId());
        } catch (TokenDoesNotExistException e) {
            Assert.fail();
        }
    }

    //Basic Turns
    @Test
    public void turn_turnRight_returnsTrueAndTankRight() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);
    }

    @Test
    public void turn_turnLeft_returnsTrueAndTankLeft() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(tank.getDirection() == Direction.Left);
    }

    @Test
    public void turn_turnUp_returnsTrueAndTankUp() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    //Check multiple turns
    @Test
    public void turn_turnDownFromLeft_returnsTrueAndTankDown() throws Exception {
        Tank tank = repo.join("");
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
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        thrown.expect(TokenDoesNotExistException.class);
        thrown.expectMessage("Token '1000' does not exist");
        repo.turn(1000, Direction.Right);
    }

    @Test
    public void turn_DownFromUp_returnsFalseAndTankUp() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertFalse(repo.turn(tank.getId(), Direction.Down));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
    }

    @Test
    public void turn_RightFromLeft_returnsFalseAndTankLeft() throws Exception {
        Tank tank = repo.join("");
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
        Tank tank = repo.join("");
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
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertTrue(repo.move(tank.getId(), Direction.Up));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[11][12]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Down));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[11][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankDown_ReturnsTrueTankMoves() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");

        repo.turn(tank.getId(), Direction.Left);
        TimeUnit.MILLISECONDS.sleep(500);
        repo.turn(tank.getId(), Direction.Down);
        Assert.assertTrue(tank.getDirection() == Direction.Down);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Up));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[11][12]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Down));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[11][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankRight_ReturnsTrueTankMoves() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Right));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][13]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Left));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][13]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    @Test
    public void move_ValidMoveTankLeft_ReturnsTrueTankMoves() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");

        repo.turn(tank.getId(), Direction.Left);
        Assert.assertTrue(tank.getDirection() == Direction.Left);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Right));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][12]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][13]);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.move(tank.getId(), Direction.Left));
        Assert.assertFalse(tank.getIntValue() == repo.getGrid()[12][13]);
        Assert.assertTrue(tank.getIntValue() == repo.getGrid()[12][12]);
    }

    //Test Breaking Constraints
    @Test
    public void move_SidewaysMoveTankUp_ReturnsTrueFalse() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertFalse(repo.move(tank.getId(), Direction.Right));

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(tank.getId(), Direction.Left));
    }

    @Test
    public void move_SidewaysMoveTankRight_ReturnsFalse() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(tank.getId(), Direction.Up));

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(tank.getId(), Direction.Down));
    }

    @Test
    public void move_ConsecutiveMoves_ReturnsFalse() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertTrue(repo.move(tank.getId(), Direction.Up));
        Assert.assertFalse(repo.move(tank.getId(), Direction.Down));
    }

    //Test Hitting Walls
    @Test
    public void move_tankHitsWall_returnsFalse() throws Exception{
        repo.setMapPath("BoxedIn.json");
        repo.setTankSpawn(0, 0);
        Tank tank = repo.join("");

        repo.turn(tank.getId(), Direction.Right);
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(tank.getId(), Direction.Right));
    }

    @Test
    public void fire_bulletFired_returnsTrue() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(15, 15);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(1000);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_consecutiveBulletsFired_returnsFalse() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(15, 15);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_thirdBulletFired_returnsFalse() throws Exception {
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(15, 15);
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(1000);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(1000);
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }

    @Test
    public void testLeave() throws Exception {

    }
}