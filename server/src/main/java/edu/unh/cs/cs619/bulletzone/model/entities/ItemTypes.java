package edu.unh.cs.cs619.bulletzone.model.entities;

public enum ItemTypes {
    NO_ITEM(0),
    ANTI_GRAV(2002),
    FUSION_REACTOR(2003),
    COIN(7),
    DEFLECTOR_SHIELD(3131),
    REPAIR_KIT(3141);

    private final int value;

    ItemTypes(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
