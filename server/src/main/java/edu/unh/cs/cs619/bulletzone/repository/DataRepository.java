package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.item.GameItemRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides tailored access to objects that are needed by the REST API/Controller
 * classes. The idea is that it will interface with a BulletZoneData instance as well as
 * any other objects it needs to answer requests having to do with users, items, accounts,
 * permissions, and other things that are related to what is stored in the database.
 *
 * The convention is that actual objects will be returned by the DataRepository so that internal
 * objects can make effective use of the results as well as the Controllers. This means that
 * all API/Controller classes will need to translate these objects into the strings they need
 * to communicate information back to the caller.
 */
//Note that the @Component annotation below causes an instance of a DataRepository to be
//created and used for the Controller classes in the "web" package.
@Component
public class DataRepository {
    private BulletZoneData bzdata;

    DataRepository() {
        //TODO: Replace database name, username, and password with what's appropriate for your group
        String url = "stman1.cs.unh.edu";
        String username = "oberon";
        String password = "pibyad2ObIb";
//
//        bzdata = new BulletZoneData(url, username, password);
        bzdata = new BulletZoneData(); //just use in-memory database
    }

    /**
     * Stub for a method that would create a user or validate the user. If a create boolean of
     * true is passed into this function, it calls createUser() with the username and password
     * parameters, creates a new bank account, and gives them a starting bank account of 1000
     * credits. The validateUser function as well as the createUser function both return null
     * already if there are issues creating or validating a user, so directly returning what they
     * return in their functions handles errors.
     * @param username Username for the user to create or validate
     * @param password Password for the user
     * @param create true if the user should be created, or false otherwise
     * @return GameUser corresponding to the username/password if successful, null otherwise
     */
    public GameUser validateUser(String username, String password, boolean create) {
        //TODO: something that invokes users.createUser(name, password) or
        //      users.validateLogin(name, password) as appropriate, maybe does other bookkeeping
        if (create) {
            GameUser newUser = bzdata.users.createUser(username, username, password);
            BankAccount bankAcc = bzdata.accounts.create();
            bankAcc.setOwner(newUser);
            bzdata.accounts.modifyBalance(bankAcc, 1000);
            return newUser;
        }
        return bzdata.users.validateLogin(username, password);
    }
}
