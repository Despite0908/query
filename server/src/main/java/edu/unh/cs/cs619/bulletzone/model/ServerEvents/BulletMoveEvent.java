package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * GridEvent implementation for movement of bullet.
 * @author Anthony Papetti
 */
public class BulletMoveEvent extends GridEvent{
    private final long tokenID;
    private final Direction direction;
    private final int intVal;
    private final int[][] grid;

    /**
     * Constructor for BulletMoveEvent. Stores necessary information for the event.
     * @param tokenID The ID of the token that the bullet belongs to
     * @param direction The direction of the bullet.
     * @param intVal Integer value of the bullet. See {@link Bullet#getIntValue() getIntValue} for value.
     * @param grid
     */
    public BulletMoveEvent(long tokenID, Direction direction, int intVal, int[][] grid) {
        super();
        this.tokenID = tokenID;
        this.direction = direction;
        this.intVal = intVal;
        this.grid = grid;
    }

    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", getMillis());
        eventJSON.put("eventType", "bulletMove");
        eventJSON.put("tokenID", tokenID);
        eventJSON.put("direction", Direction.toByte(direction));
        eventJSON.put("intVal", intVal);
        int newPos = 0;
        boolean found = false;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == intVal) {
                    newPos = (i * 16) + j;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (found) {
            eventJSON.put("newPos", newPos);
            return eventJSON.toString();
        }
        return null;
    }
}
