package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

public class TokenLeaveEvent extends GridEvent {
    private final long tokenID;
    private final int intVal;

    /**
     * Constructor for TokenLeaveEvent.
     * @param tokenID The ID of the token that's leaving
     * @param intVal The Int Value of the token that's leaving
     */
    public TokenLeaveEvent(long tokenID, int intVal) {
        super();
        this.tokenID = tokenID;
        this.intVal = intVal;
    }

    /**
     * {@inheritDoc}. Type specific fields are "intVal" and "tokenID".
     * @return {@inheritDoc}
     */
    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", getMillis());
        eventJSON.put("eventType", "tokenLeave");
        eventJSON.put("intVal", intVal);
        eventJSON.put("tokenID", tokenID);
        return eventJSON.toString();
    }
}
