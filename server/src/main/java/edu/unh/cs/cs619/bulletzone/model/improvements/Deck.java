package edu.unh.cs.cs619.bulletzone.model.improvements;

import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;

/**
 * Deck improvement. Allows soldiers and tanks to drive over water
 * @author Anthony Papetti
 */

public class Deck extends Improvement{
    @Override
    public Improvement copy() {return new Deck();}

    @Override
    public boolean canMoveInto(PlayerToken token) {
        return true;
    }

    @Override
    public boolean isDock() {
        return true;
    }

    @Override
    public int getIntValue() {
        return 50;
    }

    /**
     * {@inheritDoc}
     * @param accountId ID of the account that will buy the improvement.
     * @return {@inheritDoc}
     */
    @Override
    public boolean buyImprovement(int accountId) {
        return BankLinker.spendCredits(accountId, 80);
    }

    @Override
    public boolean sellImprovement(int accountId) {
        return BankLinker.addCredits(accountId, 80);
    }
}
