package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.entities.Builder;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;

public class Player {

    private Tank tank;
    private Builder builder;

    private Soldier soldier;

    public Player() {
        tank = null;
        builder = null;
        soldier = null;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public Tank getTank() {
        return tank;
    }
    public Builder getBuilder() {
        return builder;
    }
    public void setBuilder(Builder builder) {
        this.builder = builder;
    }

    public void setSoldier(Soldier soldier) {
        this.soldier = soldier;
    }

    public Soldier getSoldier() {
        return soldier;
    }
}
