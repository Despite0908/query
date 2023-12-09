package edu.unh.cs.cs619.bulletzone.rest;

public class HealthUpdateEvent {
    private int tankHealth;
    private int soldierHealth;
    private int builderHealth;

    public HealthUpdateEvent(int tankHealth, int soldierHealth, int builderHealth) {
        this.tankHealth = tankHealth;
        this.soldierHealth = soldierHealth;
        this.builderHealth = builderHealth;
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
}

