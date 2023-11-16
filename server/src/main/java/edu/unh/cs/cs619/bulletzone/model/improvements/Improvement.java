package edu.unh.cs.cs619.bulletzone.model.improvements;

import edu.unh.cs.cs619.bulletzone.model.PlayerToken;

public abstract class Improvement {

    public boolean isSolid() {
        return false;
    }

    public void moveInto(PlayerToken token) {
        return;
    }

    public long mutateTime(long millis) {
        return millis;
    }

    public boolean isDock() { return false;}

    public abstract int getIntValue();

    public abstract Improvement copy();
}
