package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;

/**
 * GridEvent implementation for movement of bullet.
 * @author Anthony Papetti
 */
public class BulletMoveEvent implements GridEvent{
    long millis;
    int tankID;
    Direction direction;
    int intVal;
    int[][] grid;

    /**
     * Constructor for BulletMoveEvent. Stores necessary information for the event.
     * @param tankID The ID of the tank that the bullet belongs to
     * @param direction The direction of the bullet.
     * @param intVal Integer value of the bullet. See {@link Bullet#getIntValue() getIntValue} for value.
     * @param grid
     */
    public BulletMoveEvent(int tankID, Direction direction, int intVal, int[][] grid) {
        this.millis = System.currentTimeMillis();
        this.tankID = tankID;
        this.direction = direction;
        this.intVal = intVal;
        this.grid = grid;
    }

    @Override
    public long getMillis() {
        return millis;
    }

    @Override
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.append("millis", millis);
        eventJSON.append("eventType", "bulletMove");
        eventJSON.append("tankID", tankID);
        eventJSON.append("direction", Direction.toByte(direction));
        eventJSON.append("intVal", intVal);
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
            eventJSON.append("newPos", newPos);
            return eventJSON.toString();
        }
        return null;
    }
}
