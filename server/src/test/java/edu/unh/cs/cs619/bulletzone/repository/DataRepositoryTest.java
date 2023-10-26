package edu.unh.cs.cs619.bulletzone.repository;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Iterator;

import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccountRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;

/**
 * Tests all cases of DataRepository's validateUser, including the creation of the bank accounts for
 * the users, the returning of null in specific cases, and the returning of old/new users in specific
 * cases.
 * @author Nicolas Karpf
 */
public class DataRepositoryTest {


    @Test
    public void validateUser_withCreateStatusTrue_returnsNewAccount() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", true);
        assert(newUser != null);
    }

    @Test
    public void validateUser_withCreateStatusTrueandDuplicateUsername_returnsNull() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", true);
        assert(newUser != null);
        GameUser newUser2 = repository.validateUser("nicolas", "1234", true);
        assert(newUser2 == null);
    }

    @Test
    public void validateUser_withCreateStatusFalseAndNoExistingUser_returnsNull() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", false);
        assert(newUser == null);
    }

    @Test
    public void validateUser_withCreateStatusFalseAndExistingUser_returnsExistingUser() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", true);
        assert(newUser != null);
        GameUser newUser2 = repository.validateUser("nicolas", "karpf", false);
        assert(newUser == newUser2);
    }

    @Test
    public void validateUser_withCreateStatusTrueAndNoExistingUser_createsNewBankAccount() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", true);
        assert(newUser != null);
        assert(!newUser.getOwnedAccounts().isEmpty());
        assert(newUser.getOwnedAccounts().size() == 1);
    }

    @Test
    public void validateUser_withCreateStatusTrue_givesStartingCreditsOf1000() {
        DataRepository repository = new DataRepository();
        GameUser newUser = repository.validateUser("nicolas", "karpf", true);
        Iterator<BankAccount> it = newUser.getOwnedAccounts().iterator();
        while (it.hasNext()) {
            BankAccount account = it.next();
            assert(account.getBalance() == 1000);
        }
    }
}