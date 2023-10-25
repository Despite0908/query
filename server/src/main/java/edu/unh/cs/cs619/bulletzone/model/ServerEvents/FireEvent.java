package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

/**
 * Implementation of GridEvent for a tank firing
 * @author Anthony Papetti
 */
public class FireEvent implements GridEvent{
    long millis;
    long tankID;

    /**
     * Constructor for FireEvent, stores tank's ID.
     * @param tankID The ID of the tank that is firing.
     */
    public FireEvent(long tankID) {
        this.millis = System.currentTimeMillis();
        this.tankID = tankID;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public long getMillis() {
        return millis;
    }

    /**
     * {@inheritDoc}. Type specific fields are "tankID".
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", millis);
        eventJSON.put("eventType", "fire");
        eventJSON.put("tankID", tankID);
        return eventJSON.toString();
    }
}