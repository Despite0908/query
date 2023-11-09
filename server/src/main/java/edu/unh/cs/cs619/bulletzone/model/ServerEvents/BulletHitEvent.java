package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Wall;

/**
 * Implementation of GridEvent for an object being hit.
 * @author Anthony Papetti
 */
public class BulletHitEvent extends GridEvent{
    private final long intVal;
    private final boolean destroyed;
    private final int otherIntVal;

    /**
     * Constructor for BulletHitEvent, stores necessary information for event.
     * @param intVal Integer value of bullet. See {@link Bullet#getIntValue() getIntValue} for value.
     * @param destroyed Whether the object that was hit is destroyed.
     * @param otherIntVal Integer value of object that is hit.
     *                    See {@link Bullet#getIntValue() getIntValue} or {@link Wall#getIntValue()} for value.
     */
    public BulletHitEvent(long intVal, boolean destroyed, int otherIntVal) {
        super();
        this.intVal = intVal;
        this.destroyed = destroyed;
        this.otherIntVal = otherIntVal;
    }

    /**
     * {@inheritDoc}. Type-specific fields are "bulletIntVal", "hitOjectVal", and "destroyed".
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", getMillis());
        eventJSON.put("eventType", "hit");
        eventJSON.put("bulletIntVal", intVal);
        eventJSON.put("hitObjectIntVal", otherIntVal);
        if (destroyed) {
            eventJSON.put("destroyed", 1);
        } else {
            eventJSON.put("destroyed", 0);
        }
        return eventJSON.toString();
    }
}
