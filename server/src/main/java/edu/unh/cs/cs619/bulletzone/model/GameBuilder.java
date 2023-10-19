package edu.unh.cs.cs619.bulletzone.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameBuilder {
    Map<Integer, FieldEntity> entityMap;

    public GameBuilder setWall(int pos) {
        entityMap.put(pos, new Wall(pos, 1000));
        return this;
    }
    public GameBuilder setWall(int pos, int destructVal) {
        entityMap.put(pos, new Wall(pos, destructVal));
        return this;
    }

    public Game build() {
        Game g = new Game();
        Iterator<Integer> keys = entityMap.keySet().iterator();
        while (keys.hasNext()) {
            Integer curr = keys.next();
            g.getHolderGrid().get(curr).setFieldEntity(entityMap.get(curr));
        }
        return g;
    }
}
