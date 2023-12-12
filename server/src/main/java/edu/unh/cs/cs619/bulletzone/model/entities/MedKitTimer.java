package edu.unh.cs.cs619.bulletzone.model.entities;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Game;

public class MedKitTimer extends TimerTask {
    Object monitor;
    PlayerToken token;
    Item itemTimed;
    Game game;

    /**
     * Constructor. Passes values for task.
     * @param monitor Monitor to synchronize task
     * @param token Token bullet is fired from
     * @param itemPassed Bullet to move every step
     */
    public MedKitTimer(Object monitor, Item itemPassed, PlayerToken token) {
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
        synchronized (monitor) {
            int currSeconds = token.getMedKitTimerCurrentSeconds();
            if (currSeconds == 0) {
                // TODO AIDEN Need to trigger backup medkits if any
                this.cancel();
            } else {
                token.setMedKitTimerCurrentSeconds(currSeconds - 1);
                if (token.getLife() == token.getMaxLife()) {
                    //Already at max life
                } else {
                    int currentHealth = token.getLife();
                    token.setLife(currentHealth + 1);
                }
            }

        }
    }


}
