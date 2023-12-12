package edu.unh.cs.cs619.bulletzone.rest;

public class HealthUpdateEvent {
    private int tankHealth;
    private int soldierHealth;
    private int builderHealth;
    private int shieldHealth;

    public HealthUpdateEvent(int tankHealth, int soldierHealth, int builderHealth, int shieldHealth) {
        this.tankHealth = tankHealth;
        this.soldierHealth = soldierHealth;
        this.builderHealth = builderHealth;
        this.shieldHealth = shieldHealth;
    }

    // Getters
    public int getTankHealth() {
        return tankHealth;
    }

    public int getSoldierHealth() {
        return soldierHealth;
    }

    public int getBuilderHealth() {
        return builderHealth;
    }

    public int getShieldHealth() {
        return shieldHealth;
    }
}

