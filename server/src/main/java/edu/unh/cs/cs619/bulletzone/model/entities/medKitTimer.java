package edu.unh.cs.cs619.bulletzone.model.entities;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletHitEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;
import edu.unh.cs.cs619.bulletzone.model.improvements.Improvement;

public class medKitTimer extends TimerTask {
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
    public medKitTimer(Object monitor, Item itemPassed, PlayerToken token) {
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
