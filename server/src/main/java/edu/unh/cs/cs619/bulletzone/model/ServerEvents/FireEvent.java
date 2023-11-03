package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

/**
 * Implementation of GridEvent for a token firing
 * @author Anthony Papetti
 */
public class FireEvent implements GridEvent{
    long millis;
    long tokenID;

    /**
     * Constructor for FireEvent, stores token's ID.
     * @param tokenID The ID of the token that is firing.
     */
    public FireEvent(long tokenID) {
        this.millis = System.currentTimeMillis();
        this.tokenID = tokenID;
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
     * {@inheritDoc}. Type specific fields are "tokenID".
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", millis);
        eventJSON.put("eventType", "fire");
        eventJSON.put("tokenID", tokenID);
        return eventJSON.toString();
    }
}
