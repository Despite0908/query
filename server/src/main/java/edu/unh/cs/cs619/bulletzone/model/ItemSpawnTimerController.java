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
    /**
     * Constructor. Passes values for task.
     */
    ItemSpawnTimerController() {
        //timer.schedule(new ItemSpawnTimer(), 0, ITEM_PERIOD);
    }

    private void injectItemSpawned(Game game) {

    }

    public static void createTimer(Game passedGame) {
        timer.schedule(new ItemSpawnTimer(passedGame), 0, ITEM_PERIOD);
    }
}
