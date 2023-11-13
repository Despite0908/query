package edu.unh.cs.cs619.bulletzone.model;

import org.json.JSONException;
import org.json.JSONObject;

public class EventHandler {
    TokenFactory factory = TokenFactory.getInstance();

    /**
     * This function works to handle the different event cases that effect the game board.
     * @param event JSONObject formatted event to read and update the board/HashMap collection of
     *              TokenFactory accordingly.
     * @param grid The grid of the game to update when necessary.
     * @throws JSONException Uses JSONObjects, so although I have if-else-if statements to handle
     * whether a given JSONObject has a specific keys, it's good practice to throw an exception in
     * case.
     */
    public void eventCase(JSONObject event, int[][] grid) throws JSONException {
        String eventType = event.get("eventType").toString();
        if (eventType.equals("addToken")) {
            long id = stringToLong(event.get("intVal").toString());
            int position = stringToInt(event.get("position").toString());
            factory.makeToken(id, position);
            grid[position / 16][position % 16] = (int)id;
        } else if (eventType.equals("bulletMove")) {
            long id = stringToLong(event.get("tokenID").toString());
            int position = stringToInt(event.get("position").toString());
            factory.makeToken(id, position);
            grid[position / 16][position % 16] = (int)id;
        } else if (eventType.equals("tokenLeave")) {
            long id = stringToLong(event.get("tokenID").toString());
            int tokenPosition = factory.getTokenPosition(id);
            if (tokenPosition >= 0) {
                grid[tokenPosition / 16][tokenPosition % 16] = 0;
                factory.removeToken(id);
            }
        } else if (eventType.equals("tokenMove")) {
            if (event.has("newPos")) {
                long id = stringToLong(event.get("tokenID").toString());
                int position = stringToInt(event.get("newPos").toString());
                factory.makeToken(id, position);
                grid[position / 16][position % 16] = (int)id;
            }
        }
    }

    /**
     * This function converts strings to ints. It is being called here just to make the eventCase
     * function look cleaner.
     * @param str String to change into type int
     * @return int type of String parameter passed.
     */
    public int stringToInt(String str) {
        return Integer.parseInt(str);
    }

    /**
     * This function converts strings to longs. It is being called here just to make the eventCase
     * function look cleaner.
     * @param str String to change into type long
     * @return long type of String parameter passed.
     */
    public long stringToLong(String str) {
        return Long.parseLong(str);
    }
}
