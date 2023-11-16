package edu.unh.cs.cs619.bulletzone.model;

public class Item extends FieldEntity {


    private int itemType;
    private int itemId;
    private int gridLocation;

    public Item(int itemType, int location) {
        this.setItemType(itemType);
        this.setGridLocation(location);
    }

    @Override
    public int getIntValue() {
        return (int) (itemType);
    }

    @Override
    public String toString() {
        return "I";
    }

    @Override
    public FieldEntity copy() {
        return new Item(itemType, gridLocation);
    }

    public void setGridLocation(int newLocation) {
        this.gridLocation = newLocation;
    }

    public int getGridLocation() {
        return gridLocation;
    }

    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemType() {
        return itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int movedIntoBy(PlayerToken other) {
        //TODO aiden how to powerup tank after ran over
        //TODO aiden remove from item concurrentHashMap (Dont have access to game)
        return 1;
    }

}
