package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

/**
 * An Interface that stores events that happen on the game board and
 * prints them to a JSON string.
 * @author Anthony Papetti
 */
public interface GridEvent {

    /**
     * Returns the JSON representation of the event. Fields "millis" and "eventType" will
     * be in every implementation.
     * @return The JSON representation of the event
     */
    public String toJSON();

    /**
     * Gets timestamp of event in milliseconds.
     * @return Timestamp of event in milliseconds
     */
    public long getMillis();
}
