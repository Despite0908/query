package edu.unh.cs.cs619.bulletzone.model.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;

import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;

public abstract class FieldEntity {
    //protected static final EventBus eventBus = new EventBus();
    protected FieldHolder parent;
    private boolean isItem = false;
    private final long id;

    public FieldEntity(long id) {
        this.id = id;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    /**
     * Serializes the current {@link FieldEntity} instance.
     *
     * @return Integer representation of the current {@link FieldEntity}
     */
    public abstract int getIntValue();

    public boolean getIsItem() {
        return isItem;
    }

    public void setIsItem(boolean isItemSetter) {
        isItem = isItemSetter;
    }

    public FieldHolder getParent() {
        return parent;
    }

    public void setParent(FieldHolder parent) {
        this.parent = parent;
    }

    public abstract FieldEntity copy();

    /**
     * Method to apply effects to the object that has been hit.
     * @param damage Damage done by the bullet.
     * @param game Current game.
     * @return Is token destroyed or not.
     */
    public boolean hit(int damage, Game game) {
        return true;
    }

    /**
     * Method to apply effects to the object that has been moved into.
     * @param other Token that has moved into this entity
     * @return 0: move was not successful. 1: Move was successful. 2: Re-entry.
     */
    public abstract int movedIntoBy(PlayerToken other);

    /*public static final void registerEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.register(listener);
    }

    public static final void unregisterEventBusListener(Object listener) {
        checkNotNull(listener);
        eventBus.unregister(listener);
    }*/
}
