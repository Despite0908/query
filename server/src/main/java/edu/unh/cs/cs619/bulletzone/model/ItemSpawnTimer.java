package edu.unh.cs.cs619.bulletzone.model;

import java.util.TimerTask;

/**
 * Timer Task for Updating the positions of bullets
 * @author Aiden Landman
 */
public class ItemSpawnTimer extends TimerTask {
    Object monitor;
    Game theGame;
    /**
     * Constructor. Passes values for task.
     */
    public ItemSpawnTimer(Game passedGame) {
        super();
        this.theGame = passedGame;
    }

    /**
     * Task to be run every timer step. Handles spawning items
     */
    @Override
    public void run() {
        //synchronized (monitor) {
            // System.out.println("Timer for power up spawn chance");

            // placeholder for adding logic for the possibility of a powerup spawn
            if (0 == 1) {
                Item myTestItem = new Item(1);
                FieldHolder fieldElement = theGame.getHolderGrid().get(64);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(myTestItem);
                    myTestItem.setParent(fieldElement);
                    System.out.println("Added item");
                } else {
                    System.out.println("ERROR: Could not add item");
                }
            }

        //}
    }
}
