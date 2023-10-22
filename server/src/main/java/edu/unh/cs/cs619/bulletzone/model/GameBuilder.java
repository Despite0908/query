package edu.unh.cs.cs619.bulletzone.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameBuilder {
    private static final int FIELD_DIM = 16;

    Map<Integer, FieldEntity> entityMap;

    public GameBuilder() {
        this.entityMap = new HashMap<>();
    }

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
        createFieldHolderGrid(g);
        Iterator<Integer> keys = entityMap.keySet().iterator();
        while (keys.hasNext()) {
            Integer curr = keys.next();
            g.getHolderGrid().get(curr).setFieldEntity(entityMap.get(curr));
        }
        return g;
    }

    private void createFieldHolderGrid(Game game) {
        game.getHolderGrid().clear();
        for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
            game.getHolderGrid().add(new FieldHolder());
        }

        FieldHolder targetHolder;
        FieldHolder rightHolder;
        FieldHolder downHolder;

        // Build connections
        for (int i = 0; i < FIELD_DIM; i++) {
            for (int j = 0; j < FIELD_DIM; j++) {
                targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                        + ((j + 1) % FIELD_DIM));
                downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                        * FIELD_DIM + j);

                targetHolder.addNeighbor(Direction.Right, rightHolder);
                rightHolder.addNeighbor(Direction.Left, targetHolder);

                targetHolder.addNeighbor(Direction.Down, downHolder);
                downHolder.addNeighbor(Direction.Up, targetHolder);
            }
        }
    }
}
