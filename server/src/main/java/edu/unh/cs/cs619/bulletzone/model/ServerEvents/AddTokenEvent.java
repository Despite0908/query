package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

public class AddTokenEvent implements GridEvent {

    long millis;
    int tokenVal;
    int position;

    /**
     * Constructor for AddTokenEvent, stores token's Intval.
     * @param tokenVal The ID of the token that is added.
     * @param position The position of the newly added tank
     */
    public AddTokenEvent(int tokenVal, int position) {
        this.millis = System.currentTimeMillis();
        this.tokenVal = tokenVal;
        this.position = position;
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
        eventJSON.put("eventType", "addToken");
        eventJSON.put("intVal", tokenVal);
        eventJSON.put("position", position);
        return eventJSON.toString();
    }
}
