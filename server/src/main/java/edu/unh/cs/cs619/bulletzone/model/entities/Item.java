package edu.unh.cs.cs619.bulletzone.model.entities;

import com.google.common.eventbus.EventBus;

import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.events.CustomEvent;
import edu.unh.cs.cs619.bulletzone.events.CustomEventTypes;
import edu.unh.cs.cs619.bulletzone.model.BankLinker;
import edu.unh.cs.cs619.bulletzone.model.BulletTimer;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;

public class Item extends FieldEntity {


    private ItemTypes itemType;
    private int gridLocation;

    //public Item(long id, int itemType, int location) {
    public Item(long id, ItemTypes theItemType, int location) {
        super(id);
        this.setItemType(theItemType);
        this.setGridLocation(location);
        this.setIsItem(true);
    }



    @Override
    public int getIntValue() {
        return (int) (40000000 + 10000 * getId() + getItemType().getValue());
    }

    /**
     * {@inheritDoc}
     * @param damage Damage done by the bullet.
     * @param game Current game.
     * @return True
     */
    @Override
    public boolean hit(int damage, Game game) {
        EventHistory eventHistory = EventHistory.get_instance();
        getParent().clearField();
        setParent(null);
        game.decrementItems();
        eventHistory.addEvent(new TokenLeaveEvent(getId(), getIntValue()));
        return true;
    }

    @Override
    public String toString() {
        return "I";
    }

    @Override
    public FieldEntity copy() {
        return new Item(getId(), itemType, gridLocation);
    }

    public void setGridLocation(int newLocation) {
        this.gridLocation = newLocation;
    }

    public int getGridLocation() {
        return gridLocation;
    }

    public ItemTypes getItemType() {
        return itemType;
    }
    public void setItemType(ItemTypes itemType) {
        this.itemType = itemType;
    }

    /**
     * {@inheritDoc}
     * @param other Token that has moved into this entity
     * @return 1: Move was successful
     */
    public int movedIntoBy(PlayerToken other) {
        EventHistory.get_instance().addEvent(new TokenLeaveEvent(this.getId(), this.getIntValue()));
        CustomEvent customEvent = new CustomEvent(CustomEventTypes.ANTI_GRAV_PICKUP, this);
        if (getItemType() == ItemTypes.FUSION_REACTOR) {
            Item addingItem = new Item(this.getId(), ItemTypes.FUSION_REACTOR, this.gridLocation);
            other.numBulletsAfterReactor();
            other.fireRateAfterReactor();
            other.movementSpeedAfterReactor();
            other.storePowerUp(addingItem);
        } else if (getItemType() == ItemTypes.ANTI_GRAV) {
            Item addingItem = new Item(this.getId(), ItemTypes.ANTI_GRAV, this.gridLocation);
            other.movementSpeedAfterAntiGrav();
            other.fireRateAfterAntiGrav();
            other.storePowerUp(addingItem);
        } else if (getItemType() == ItemTypes.COIN) {
            int credits = ThreadLocalRandom.current().nextInt(10, 100 + 1);
            BankLinker.addCredits(other.getAccountID(), credits);
        } else if (getItemType() == ItemTypes.DEFLECTOR_SHIELD) {
            //Implement effects of Deflector Shield
            Item addingItem = new Item(this.getId(), ItemTypes.DEFLECTOR_SHIELD, this.gridLocation);
            other.storePowerUp(addingItem);


        } else if (getItemType() == ItemTypes.REPAIR_KIT) {
            //Implement effects of RepairKit

            Item addingItem = new Item(this.getId(), ItemTypes.REPAIR_KIT, this.gridLocation);
            other.storePowerUp(addingItem);
        }
        // this was working
        // eventBus.post(customEvent);
        EventBus eventBus = BusProvider.BusProvider().eventBus;
        eventBus.post(customEvent);
        return 1;
    }


//    public boolean droppedBy(PlayerToken other) {
//        if (!other.heldItems.contains(this)) {
//            return false;
//        }
//    }
}
