package edu.unh.cs.cs619.bulletzone.model;

import java.util.Collection;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.account.BankAccount;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;

/**
 * links to BulletZoneData for purposes of updating the number of credits in the account.
 * @author Anthony Papetti
 */
public class BankLinker {

    static BulletZoneData data = DataRepository.get_instance().getbzData();

    public static int[] getInventory(int id){
        int [] inventory = new int[1];
        int balance = getBalance(id);
        inventory[0] = balance;
        return inventory;
    }

    /**
     * Gets the current balance in the users account.
     * @param id The account ID to get credits from
     * @return Returns current balance if there is a user. Returns 0 otherwise.
     */
    public static int getBalance(int id) {
        GameUser user = data.users.getUser(id);
        try {
            Collection<BankAccount> accounts = user.getOwnedAccounts();
            BankAccount ba = accounts.iterator().next();
            double balance = ba.getBalance();
            return (int) balance;
        } catch (NullPointerException e) {
            System.out.println("Error: Getting coins, user not found");
        }
        return 0;
    }

    /**
     * Adds credits to the current user's account.
     * @param id The account ID to get credits from
     * @param credits The number of credits to be addded.
     * @return True if successful. False if there is no current user.
     */
    public static boolean addCredits(int id, int credits) {
        GameUser user = data.users.getUser(id);
        try {
            Collection<BankAccount> accounts = user.getOwnedAccounts();
            BankAccount ba = accounts.iterator().next();
            data.accounts.modifyBalance(ba, credits);
        } catch (NullPointerException e) {
            System.out.println("Error: Adding coins, user not found");
            return false;
        }
        return true;
    }

    /**
     * Subtracts credits to the current user's account.
     * @param id The account ID to get credits from
     * @param credits The number of credits to be spent.
     * @return True if successful. False if there is no current user.
     */
    public static boolean spendCredits(int id, int credits) {
        GameUser user = data.users.getUser(id);
        try {
            Collection<BankAccount> accounts = user.getOwnedAccounts();
            BankAccount ba = accounts.iterator().next();
            double balance = ba.getBalance();
            if (credits > balance) {
                return false;
            }
            data.accounts.modifyBalance(ba, -credits);
        } catch (NullPointerException e) {
            System.out.println("Error: Adding coins, user not found");
            return false;
        }
        return true;
    }

    public void seamBZData(DataRepository dataRepo) {
        data = dataRepo.getbzData();
    }
}
