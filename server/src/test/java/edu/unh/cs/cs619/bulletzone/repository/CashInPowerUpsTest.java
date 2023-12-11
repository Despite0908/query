package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

public class CashInPowerUpsTest {
    @InjectMocks
    InMemoryGameRepository repo;

    DataRepository dataRepo;

    static BankLinker testLinker;

    static GameUser baseUser;

    static BankAccount baseAccount;

    static BulletZoneData db;
    @Before
    public void setUp() throws Exception {
        repo = new InMemoryGameRepository();
        repo.setMapPath("PowerUpTestMap.json");
        repo.setTankSpawn(15, 0);
        db = new BulletZoneData();
        db.rebuildData();
        dataRepo = DataRepository.testingInstance();
        dataRepo.seamBZData(db);
        testLinker = new BankLinker();
        testLinker.seamBZData(dataRepo);
        baseUser = db.users.createUser("BasicUser", "BasicUser", "password");
        baseAccount = db.accounts.create();
        db.accounts.modifyBalance(baseAccount, 1000);
        db.permissions.setOwner(baseAccount, baseUser);
    }

    @Test
    public void cashInPowerUp_afterAcquiringPowerUp_addsCredits() throws Exception {
        Tank tank = repo.join("BasicUser", baseUser.getId()).getTank();
        Assert.assertNotNull(tank);
        Assert.assertEquals(1000, baseAccount.getBalance(), 0);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        tank.cashInPowerUpsTestFunction(testLinker);
        Assert.assertNotEquals(1000, baseAccount.getBalance());
    }

    @Test
    public void storePowerUp_afterAcquiringPowerUp_addsToHeldItems() throws Exception {
        Tank tank = repo.join("BasicUser", baseUser.getId()).getTank();
        Assert.assertNotNull(tank);
        Assert.assertEquals(repo.move(tank.getId(), Direction.Up), 1);
        Assert.assertEquals(1, tank.getHeldItems().size());
    }
}
