package edu.unh.cs.cs619.bulletzone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Representation of data from a "Map" file
 * @author Anthony Papetti
 */

public class GameMap {
    /**
     * A helper class that represents data from walls.
     */
    public class WallLoader {
        private int pos;
        private int destructVal;

        /**
         * Constructor that sets values for position and destruct value
         * @param pos Position
         * @param destructVal Destruct Value
         */
        public WallLoader(int pos, int destructVal) {
            this.pos = pos;
            this.destructVal = destructVal;
        }

        /**
         * Get Position of wall.
         * @return Position of wall
         */
        public int getPos() {
            return pos;
        }

        /**
         * Get destruct value,
         * @return Destruct value
         */
        public int getDestructVal() {
            return destructVal;
        }
    }
    private List<WallLoader> wallPos;

    /**
     * Constructor that initializes wallPos.
     */
    public GameMap() {
        this.wallPos = new ArrayList<>();
    }

    /**
     * Adds wall to the map.
     * @param pos Position of the wall
     * @param destructVal Destruct Value
     */
    public void addWall(int pos, int destructVal) {
        wallPos.add(new WallLoader(pos, destructVal));
    }

    /**
     * Adds wall to the map.
     * @param pos Position of the wall
     */
    public void addWall(int pos) {
        wallPos.add(new WallLoader(pos, 1000));
    }

    /**
     * Returns a list of walls.
     * @return A list of WallLoader objects that represent the walls
     */
    public List<WallLoader> getWalls() {
        return wallPos;
    }
}
