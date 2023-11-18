package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;

public class Item extends FieldEntity {


    private int itemType;
    private int itemId;
    private int gridLocation;

    public Item(long id, int itemType, int location) {
        super(id);
        this.setItemType(itemType);
        this.setGridLocation(location);
    }

    @Override
    public int getIntValue() {
        return (int) (40000000 + 10000 * getId() + 10 * getItemType());
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
        game.getItems().remove(getId());
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

    public int getItemType() {
        return itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /**
     * {@inheritDoc}
     * @param other Token that has moved into this entity
     * @return 1: Move was successful
     */
    public int movedIntoBy(PlayerToken other) {
        //TODO aiden how to powerup tank after ran over
        //TODO aiden remove from item concurrentHashMap (Dont have access to game)
        //From Anthony: Make sure to move the tank into the Item's spot
        return 1;
    }

}
