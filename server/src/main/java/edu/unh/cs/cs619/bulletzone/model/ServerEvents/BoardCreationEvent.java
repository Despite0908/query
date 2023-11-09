package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

public class BoardCreationEvent implements GridEvent{

    private long millis;

    /**
     * Constructor. Sets timestamp.
     */
    public BoardCreationEvent() {
        this.millis = System.currentTimeMillis();
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
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", millis);
        eventJSON.put("eventType", "boardCreation");
        return eventJSON.toString();
    }
}
