package edu.unh.cs.cs619.bulletzone.model.improvements;

import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;

public abstract class Improvement {

    /**
     * Can a token move into a space with the improvement?
     * @return True if a token can, false if it can't.
     */
    public abstract boolean canMoveInto(PlayerToken token);

    /**
     * Applies changes to timestamp to make movement slower or faster.
     * @param millis The "base" timestamp
     * @return The changed timestamp
     */
    public long mutateTime(long millis) {
        return millis;
    }

    /**
     * Is improvement a dock?
     * @return If improvement is a dock, true. Else, false.
     */
    public boolean isDock() { return false;}

    /**
     * Generates integer value of improvement.
     * @return Integer value of improvement.
     */
    public abstract int getIntValue();

    /**
     * Copies improvement.
     * @return Copy of improvement
     */
    public abstract Improvement copy();

    /**
     * Subtracts the cost of the improvement from the user's account.
     * @param accountId ID of the account that will buy the improvement.
     * @return Returns false if not enough credits in account. Returns true otherwise.
     */
    public abstract boolean buyImprovement(int accountId);
}
