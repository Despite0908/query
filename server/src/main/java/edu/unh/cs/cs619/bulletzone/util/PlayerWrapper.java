package edu.unh.cs.cs619.bulletzone.util;

public class PlayerWrapper {

    private long tankId;
    private long builderId;

    public PlayerWrapper(long tankId, long builderId) {
        this.tankId = tankId;
        this.builderId = builderId;
    }

    public long getBuilderId() {
        return builderId;
    }

    public long getTankId() {
        return tankId;
    }

    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }
}
