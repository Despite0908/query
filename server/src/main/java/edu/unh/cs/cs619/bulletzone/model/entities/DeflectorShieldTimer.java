package edu.unh.cs.cs619.bulletzone.model.entities;

import java.util.TimerTask;

public class DeflectorShieldTimer extends TimerTask {
    Object monitor;
    PlayerToken token;
    Item itemTimed;

    /**
     * Constructor. Passes values for task.
     * @param monitor Monitor to synchronize task
     * @param token Token bullet is fired from
     * @param itemPassed Bullet to move every step
     */
    public DeflectorShieldTimer (Object monitor, Item itemPassed, PlayerToken token) {
        super();
        this.monitor = monitor;
        this.token = token;
        this.itemTimed = itemPassed;
    }

    /**
     * Task to be run every timer step. Handles moving bullet and hitting Field Entities
     */
    @Override
    public void run() {
        synchronized (this.monitor) {
            int currHealth = token.getShieldHealth();
            if (currHealth == 0) {
                // TODO AIDEN Need to trigger backup DeflectorShields if any
                this.cancel();
                token.processShieldRemover(itemTimed);
            } else {
                if(token.getShieldHealth() == 50) {

                } else {
                    token.setShieldHealth(currHealth + 1);
                }
            }
        }
    }


}
