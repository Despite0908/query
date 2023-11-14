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
import edu.unh.cs.cs619.bulletzone.model.Soldier;
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
        repo.setMapPath("BlankMap.json");
        repo.setTankSpawn(12, 12);
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
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);

        Assert.assertFalse(repo.move(tank.getId(), Direction.Right));

        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(tank.getId(), Direction.Left));
    }

    @Test
    public void move_SidewaysMoveTankRight_ReturnsFalse() throws Exception {
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
    public void fire_bulletFired_returnsTrue() throws Exception {;
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_consecutiveBulletsFired_returnsFalse() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }

    @Test
    public void fire_thirdBulletFired_returnsFalse() throws Exception {
        Tank tank = repo.join("");
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertTrue(repo.fire(tank.getId(), 1));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.fire(tank.getId(), 1));
    }

    //Eject Tests
    @Test
    public void eject_normalEjection_ReturnsSoldier() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
    }

    @Test
    public void eject_SomeSpace_ReturnsSoldier() throws Exception {
        Tank tank = repo.join("BoxedIn.json");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
    }

    @Test
    public void eject_noSpace_returnsNull() throws Exception {
        repo.setMapPath("TrueBox.json");
        repo.setTankSpawn(1, 1);
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNull(soldier);
    }

    @Test
    public void eject_multipleEjections_returnsNull() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Soldier soldier2 = repo.eject(tank.getId());
        Assert.assertNull(soldier2);
    }

    @Test
    public void eject_afterSoldierDestroy_returnsSoldier() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        //Destroy Soldier, doesn't remove from game.soldiers but it's fine for
        //what we're doing
        soldier.getParent().clearField();
        soldier.setParent(null);
        tank.setPair(null);
        Soldier soldier2 = repo.eject(tank.getId());
        Assert.assertNotNull(soldier2);
    }

    //Timed tests for soldiers

    @Test
    public void turn_multipleTurnsSoldier_returnsTrue() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.turn(soldier.getId(), Direction.Right));
        Assert.assertTrue(repo.turn(soldier.getId(), Direction.Down));
        Assert.assertTrue(repo.turn(soldier.getId(), Direction.Left));
        Assert.assertTrue(repo.turn(soldier.getId(), Direction.Up));
    }

    @Test
    public void move_movesAtTankStep_returnsFalse() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
        TimeUnit.MILLISECONDS.sleep(500);
        Assert.assertFalse(repo.move(soldier.getId(), Direction.Up));
    }

    @Test
    public void move_movesAfter1Sec_returnsTrue() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
        TimeUnit.MILLISECONDS.sleep(1000);
        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
    }

    @Test
    public void fire_multipleBullets_returnsFalse() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.fire(soldier.getId(), 4));
        Assert.assertFalse(repo.fire(soldier.getId(), 4));
    }

    @Test
    public void fire_after250ms_returnsTrue() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.fire(soldier.getId(), 4));
        TimeUnit.MILLISECONDS.sleep(250);
        Assert.assertTrue(repo.fire(soldier.getId(), 4));
    }

    //Re-enter test
    @Test
    public void move_soldierEntersTank_TrueAndSoldierRemoved() throws Exception {
        Tank tank = repo.join("");
        Soldier soldier = repo.eject(tank.getId());
        Assert.assertNotNull(soldier);
        Assert.assertTrue(repo.move(soldier.getId(), Direction.Down));
        Assert.assertNull(soldier.getParent());
        Assert.assertNull(tank.getPair());
    }
}