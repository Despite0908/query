package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;

/**
 * Implementation of GridEvent for a tank moving or turning.
 * @author Anthony Papetti
 */
public class TankMoveEvent implements GridEvent{
    long millis;
    long tankID;
    Direction direction;
    int intVal;
    int[][] grid;

    /**
     * Constructor for TankMoveEvent. Stores necessary event information
     * @param tankID The ID of the tank that is moving.
     * @param direction Tank's new direction
     * @param intVal Integer value of the tank. See {@link Tank#getIntValue() getIntValue} for value.
     * @param grid Integer array representation of board after tank moves.
     *             From {@link GameRepository#getGrid() GameRepository.getGrid()}.
     */
    public TankMoveEvent(long tankID, Direction direction, int intVal, int[][] grid) {
        this.millis = System.currentTimeMillis();
        this.tankID = tankID;
        this.direction = direction;
        this.intVal = intVal;
        this.grid = grid;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public long getMillis() {
        return millis;
    }

    /**
     * {@inheritDoc}. Type specific fields are "tankID", "direction", "intVal", and "newPos".
     * @return {@inheritDoc}
     */
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", millis);
        eventJSON.put("eventType", "tankMove");
        eventJSON.put("tankID", tankID);
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
