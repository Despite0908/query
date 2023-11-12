package edu.unh.cs.cs619.bulletzone.model;

public class Item extends FieldEntity {

    private int itemType;
    private int itemId;

    public Item(int itemType) {
        this.setItemType(itemType);
    }

    @Override
    public int getIntValue() {
        return (int) (3000 + itemType);
    }

    @Override
    public String toString() {
        return "I";
    }

    @Override
    public FieldEntity copy() {
        return new Item(itemType);
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

}
