package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;

/**
 * Implementation of GridEvent for a token moving or turning.
 * @author Anthony Papetti
 */
public class TokenMoveEvent implements GridEvent{
    private final long millis;
    private final long tokenID;
    private final Direction direction;
    private final int intVal;
    private final int[][] grid;

    //TODO: Update intVal docs

    /**
     * Constructor for tokenMoveEvent. Stores necessary event information
     * @param tokenID The ID of the token that is moving.
     * @param direction token's new direction
     * @param intVal Integer value of the token. See {@link Tank#getIntValue() getIntValue} for value.
     * @param grid Integer array representation of board after token moves.
     *             From {@link GameRepository#getGrid() GameRepository.getGrid()}.
     */
    public TokenMoveEvent(long tokenID, Direction direction, int intVal, int[][] grid) {
        this.millis = System.currentTimeMillis();
        this.tokenID = tokenID;
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
     * {@inheritDoc}. Type specific fields are "tokenID", "direction", "intVal", and "newPos".
     * @return {@inheritDoc}
     */
    public String toJSON() {
        JSONObject eventJSON = new JSONObject();
        eventJSON.put("millis", millis);
        eventJSON.put("eventType", "tokenMove");
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
