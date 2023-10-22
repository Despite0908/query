package edu.unh.cs.cs619.bulletzone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    public class WallLoader {
        private int pos;
        private int destructVal;
        public WallLoader(int pos, int destructVal) {
            this.pos = pos;
            this.destructVal = destructVal;
        }

        public int getPos() {
            return pos;
        }
        public int getDestructVal() {
            return destructVal;
        }
    }
    private List<WallLoader> wallPos;

    public GameMap() {
        this.wallPos = new ArrayList<>();
    }

    public void addWall(int pos, int destructVal) {
        wallPos.add(new WallLoader(pos, destructVal));
    }

    public void addWall(int pos) {
        wallPos.add(new WallLoader(pos, 1000));
    }

    public List<WallLoader> getWalls() {
        return wallPos;
    }
}
