package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

/**
 * Implementation of GridEvent for a token firing
 * @author Anthony Papetti
 */
public class FireEvent extends GridEvent{
    private final long tokenID;

    /**
     * Constructor for FireEvent, stores token's ID.
     * @param tokenID The ID of the token that is firing.
     */
    public FireEvent(long tokenID) {
        super();
        this.tokenID = tokenID;
    }

    /**
     * {@inheritDoc}. Type specific fields are "tokenID".
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", getMillis());
        eventJSON.put("eventType", "fire");
        eventJSON.put("tokenID", tokenID);
        return eventJSON.toString();
    }
}
