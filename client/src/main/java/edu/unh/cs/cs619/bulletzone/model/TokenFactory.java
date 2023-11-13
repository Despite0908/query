package edu.unh.cs.cs619.bulletzone.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * This class acts as a way to monitor tokens that are on the board and store them in a collection
 * to which we can reference when updating the grid with events from the server. This class also
 * handles said events, taking them in as a String array, reformatting them back to their original
 * JSON states, and then reading them from there. In short, this class takes over the updating of
 * the board every 100 ms based only on the events that occurred after a given timestamp (passed
 * elsewhere; this class does not worry about that).
 * @author Nicolas Karpf
 */
public class TokenFactory {

    private static TokenFactory instance;
    private HashMap<Long, Token> onFieldTokens;
    private EventHandler handler = new EventHandler();
    static int[][] grid;

    /**
     * This updates the int array with the new positions of tokens based on events pulled from the
     * server side.
     * @param newEvents String array from server-side containing JSON-structured events (cast to
     *                  toString)
     * @return int[][] representing the grid that the board calls upon for viewing
     */
    public int[][] updateGrid(String[] newEvents) {
        JSONObject event = null;
        JSONArray eventArr = null;
        try {
            eventArr = new JSONArray(newEvents);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < eventArr.length(); i++) {
            try {
                event = (JSONObject) eventArr.get(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            handler.eventCase(event, grid);
        }
        return grid;
    }

    /**
     * This function acts as a storing mechanism for Tokens for the game board. This allows easy
     * interaction with a HashMap to check if a Token is already on the field or not.
     * @param tokenID unique id of the token to make/update.
     * @param position position of the token to make/update.
     * @return Returns the newly made token if not already in this class's HashMap collection, otherwise
     * returns the existing token.
     */
    public Token makeToken(long tokenID, int position) {
        if (onFieldTokens.containsKey(tokenID)) {
            Token existingToken = onFieldTokens.get(tokenID);
            existingToken.setNewPosition(position);
            return existingToken;
        } else {
            Token newToken = new Token(tokenID, position);
            onFieldTokens.put(tokenID, newToken);
            return newToken;
        }
    }

    /**
     * This is the constructor for the TokenFactory. Because we want the factory to be a singleton,
     * we initialize the HashMap collection that it stores when it is first initialized. This is only
     * carried out if the instance of the Factory is not already initialized.
     */
    private TokenFactory() {
        onFieldTokens = new HashMap<>();
    }

    /**
     * This either initializes the instance of this class if has not already been initialized, or
     * returns the shared instance of it otherwise.
     * @return The instance of the TokenFactory to use for the application.
     */
    public static TokenFactory getInstance() {
        if (instance == null) {
            instance = new TokenFactory();
            grid = new int[16][16];
        }
        return instance;
    }

    /**
     * This function carries out the removal of a token from the HashMap collection if it is present.
     * @param tokenID The id (key) for the token (value) to be removed from the HashMap.
     * @return Returns false if the token was not in the collection/board, true otherwise
     */
    public boolean removeToken(long tokenID) {
        if (onFieldTokens.containsKey(tokenID)) {
            onFieldTokens.remove(tokenID);
            return true;
        }
        return false;
    }

    /**
     * This function returns the position of a token on the board currently. This is mainly used when
     * we need to remove a token from the board, helping us avoid iterating through a double for-loop
     * @param id id of the Token to get from the HashMap.
     * @return Returns the posiiton of the token if it is on the board (in the HashMap collection),
     * returns -1 to indicate not on the board otherwise.
     */
    public int getTokenPosition(long id) {
        if (onFieldTokens.containsKey(id)) {
            return onFieldTokens.get(id).position;
        }
        return -1;
    }
}
