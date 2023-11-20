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
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SoldierOperationsTest {

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
        long soldierIntVal = soldier.getIntValue();
        Assert.assertNotNull(soldier);
        Soldier soldier2 = repo.eject(tank.getId());
        long intVal2 = soldier.getIntValue();
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

//    @Test
//    public void move_movesAtTankStep_returnsFalse() throws Exception {
//        Tank tank = repo.join("");
//        Soldier soldier = repo.eject(tank.getId());
//        Assert.assertNotNull(soldier);
//        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
//        TimeUnit.MILLISECONDS.sleep(500);
//        Assert.assertFalse(repo.move(soldier.getId(), Direction.Up));
//    }

//    @Test
//    public void move_movesAfter1Sec_returnsTrue() throws Exception {
//        Tank tank = repo.join("");
//        Soldier soldier = repo.eject(tank.getId());
//        Assert.assertNotNull(soldier);
//        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
//        TimeUnit.MILLISECONDS.sleep(1000);
//        Assert.assertTrue(repo.move(soldier.getId(), Direction.Up));
//    }

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
//    @Test
//    public void move_soldierEntersTank_TrueAndEjectedFalse() throws Exception {
//        Tank tank = repo.join("");
//        Soldier soldier = repo.eject(tank.getId());
//        Assert.assertNotNull(soldier);
//        Assert.assertTrue(repo.move(soldier.getId(), Direction.Down));
//        Assert.assertNull(soldier.getParent());
//        Assert.assertFalse(tank.isEjected());
//    }

}