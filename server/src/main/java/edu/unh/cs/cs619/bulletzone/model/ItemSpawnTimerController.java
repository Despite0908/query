package edu.unh.cs.cs619.bulletzone.model;

import org.springframework.stereotype.Component;

import java.util.Timer;

/**
 * Timer Task for Updating the positions of bullets
 * @author Aiden Landman
 */
@Component
public class ItemSpawnTimerController {
    private static final int ITEM_PERIOD = 1000;
    private static final Timer timer = new Timer();
    Object monitor;
    /**
     * Constructor. Passes values for task.
     */
    public ItemSpawnTimerController(Object monitorPassed) {
        //timer.schedule(new ItemSpawnTimer(), 0, ITEM_PERIOD);
        this.monitor = monitorPassed;
    }

    private void injectItemSpawned(Game game) {

    }

    public void createTimer(Game passedGame) {
        timer.schedule(new ItemSpawnTimer(passedGame, monitor), 0, ITEM_PERIOD);
    }
}
