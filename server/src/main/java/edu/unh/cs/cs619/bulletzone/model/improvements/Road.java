package edu.unh.cs.cs619.bulletzone.model.improvements;

import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;

/**
 * Road improvement. Increases speed of units driving over it by 50%.
 */
public class Road extends Improvement{

    @Override
    public Improvement copy() {
        return new Road();
    }

    @Override
    public String toString() {return "R";}

    /**
     * {@inheritDoc}
     * @return 100
     */
    @Override
    public int getIntValue() {
        return 100;
    }
    //TODO: MUTATE TIME

    @Override
    public boolean canMoveInto(PlayerToken token) {
        return true;
    }

    @Override
    public long mutateTime(long millis, long interval) {
        return millis + (interval / 2);
    }

    /**
     * {@inheritDoc}
     * @param accountId ID of the account that will buy the improvement.
     * @return {@inheritDoc}
     */
    @Override
    public boolean buyImprovement(int accountId) {
        return BankLinker.spendCredits(accountId, 40);
    }

    @Override
    public boolean sellImprovement(int accountId) {
        return BankLinker.addCredits(accountId, 40);
    }
}
