package edu.unh.cs.cs619.bulletzone.model;

import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.ItemTypes;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.AddTokenEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;

/**
 * Timer Task for Updating the positions of bullets
 * @author Aiden Landman
 */
public class ItemSpawnTimer extends TimerTask {
    Object monitor;
    Game theGame;
    AtomicLong idGenerator;
    private static final int FIELD_DIM = 16;
    /**
     * Constructor. Passes values for task.
     */
    public ItemSpawnTimer(Game passedGame, AtomicLong idGenerator, Object monitor) {
        super();
        this.theGame = passedGame;
        this.idGenerator = idGenerator;
        this.monitor = monitor;
    }

    /**
     * Task to be run every timer step. Handles spawning items
     */
    @Override
    public void run() {
        //TODO
        synchronized (monitor) {
            Random randomArea = new Random();
            // placeholder for adding logic for the possibility of a powerup spawn
            if (true) {

                int numPlayers = theGame.getTanks().size();
                int numItems = theGame.getNumItems();

                // TODO aiden just here to not fill entire board remove when ready!!!!
                //if(numItems > 10) {
                //    return;
                //}

                int randomPlace = randomArea.nextInt(FIELD_DIM * FIELD_DIM);
                FieldHolder fieldElement = theGame.getHolderGrid().get(randomPlace);
                float baseProbability = 25.0f;
                float probability = baseProbability * numPlayers / (numItems + 1);
                float rangeMin = 0.0f;
                float rangeMax = 1.0f;
                Random r = new Random();
                double createdRanNum = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                createdRanNum *= 100;
                boolean successFlag = false;

                if (createdRanNum < probability) {
                    while(!successFlag) {
                        if (!fieldElement.isPresent() && !(fieldElement.isImproved() && fieldElement.getImprovement().isSolid())) {
                            Item myTestItem = new Item(idGenerator.getAndIncrement(), randomItem(), randomPlace);
                            fieldElement.setFieldEntity(myTestItem);
                            myTestItem.setParent(fieldElement);
                            //Add to event history
                            EventHistory.get_instance().addEvent(new AddTokenEvent(myTestItem.getIntValue(), randomPlace));
                            theGame.incrementItems();
                            System.out.println("Added item " + myTestItem.getItemType() + " with value " + myTestItem.getItemType().getValue());
                            successFlag = true;

                        } else {
                            randomPlace = randomArea.nextInt(FIELD_DIM * FIELD_DIM);
                            fieldElement = theGame.getHolderGrid().get(randomPlace);
                            // successFlag = true;
                        }
                    }
                }

            }

        }
    }

    /**
     * Decides the random Item we are using
     * @return int val representing the PowerUP
     */
    public ItemTypes randomItem() {
        Random randomItemNum = new Random();
        int randomItem = randomItemNum.nextInt(5) + 1;
        ItemTypes randomItemType = null;
        if (randomItem == 1) {
            //Thingamagig
            randomItemType = ItemTypes.COIN;
        } else if(randomItem == 2) {
            //AntiGrav
            randomItemType = ItemTypes.ANTI_GRAV;
        } else if(randomItem == 3) {
            //FusionReactor
            randomItemType = ItemTypes.FUSION_REACTOR;
        } else if(randomItem == 4) {
            //FusionReactor
            randomItemType = ItemTypes.REPAIR_KIT;
        } else if(randomItem == 5) {
            //FusionReactor
            randomItemType = ItemTypes.DEFLECTOR_SHIELD;
        }
        return randomItemType;
    }
}
