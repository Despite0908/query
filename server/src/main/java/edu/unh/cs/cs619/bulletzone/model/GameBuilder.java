package edu.unh.cs.cs619.bulletzone.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Builder class that creates the Game Object. Main priority is placing walls and creating
 * the game's holder grid.
 * @author Anthony Papetti
 */
public class GameBuilder {
    private static final int FIELD_DIM = 16;

    private Map<Integer, Wall> wallMap;
    private Terrain[] fieldTerrain;

    /**
     * Constructor that initializes a map of entities and an array of terrain.
     */
    public GameBuilder() {
        this.wallMap = new HashMap<>();
        fieldTerrain = new Terrain[FIELD_DIM * FIELD_DIM];
        Arrays.fill(fieldTerrain, Terrain.Normal);
    }

    /**
     * Places a wall at a position on the game. Destruct value of this wall defaults to 1000.
     * @param pos Position for wall to be placed
     * @return Returns this object
     */
    public GameBuilder setWall(int pos) {
        wallMap.put(pos, new Wall(pos, 1000));
        return this;
    }

    /**
     * Places a wall at a position on the game with a custom destruct value.
     * @param pos Position for wall to be placed
     * @param destructVal Destruct Value of wall
     * @return Returns this object
     */
    public GameBuilder setWall(int pos, int destructVal) {
        wallMap.put(pos, new Wall(pos, destructVal));
        return this;
    }

    /**
     * Add terrain to the map.
     * @param position Position terrain to be added at
     * @param terrain Type of terrain
     */
    public void addTerrain(int position, Terrain terrain) {
        fieldTerrain[position] = terrain;
    }

    /**
     * Builds the Game object
     * @return A Game object built to the specifications from this GameBuilder
     */
    public Game build() {
        Game g = new Game();
        createFieldHolderGrid(g);
        Iterator<Integer> keys = wallMap.keySet().iterator();
        while (keys.hasNext()) {
            Integer curr = keys.next();
            g.getHolderGrid().get(curr).setImprovement(wallMap.get(curr));
        }
        return g;
    }

    /**
     * A helper method to create the holder grid for the Game being built.
     * @param game The Game that the holder grid attaches to
     */
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
                //Set terrain of target Holder
                targetHolder.setTerrain(fieldTerrain[i * FIELD_DIM + j]);
            }
        }
    }
}
