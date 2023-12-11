package edu.unh.cs.cs619.bulletzone.model.improvements;

import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;

/**
 * Wall improvement. Acts as obstacle for units to move around
 * @author Anthony Papetti
 */

public class Wall extends Improvement {
    int destructValue, pos;

    public Wall(int pos, int destructValue){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    @Override
    public Improvement copy() {
        return new Wall(pos, destructValue);
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "W";
    }

    public int getPos(){
        return pos;
    }

    @Override
    public boolean canMoveInto(PlayerToken token) {
        return false;
    }

    /**
     * {@inheritDoc}
     * @param accountId ID of the account that will buy the improvement.
     * @return {@inheritDoc}
     */
    @Override
    public boolean buyImprovement(int accountId) {
        return BankLinker.spendCredits(accountId, 100);
    }
}
