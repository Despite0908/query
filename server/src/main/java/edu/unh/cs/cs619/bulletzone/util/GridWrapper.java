package edu.unh.cs.cs619.bulletzone.util;

import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;

/**
 * Created by simon on 10/1/14.
 */
public class GridWrapper {
    private int[][] grid;
    private long timeStamp;

    public GridWrapper(int[][] grid) {
        this.grid = grid;
        this.timeStamp = EventHistory.get_instance().getClock().millis();
    }

    public int[][] getGrid() {
        return this.grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
