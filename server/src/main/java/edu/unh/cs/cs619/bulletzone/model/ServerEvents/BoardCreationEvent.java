package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

public class BoardCreationEvent extends GridEvent{

    /**
     * Constructor. Sets timestamp.
     */
    public BoardCreationEvent() {
        super();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", getMillis());
        eventJSON.put("eventType", "boardCreation");
        return eventJSON.toString();
    }
}
