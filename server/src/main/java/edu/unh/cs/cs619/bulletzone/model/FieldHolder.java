package edu.unh.cs.cs619.bulletzone.model;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

import edu.unh.cs.cs619.bulletzone.model.improvements.Improvement;

public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();

    private Optional<Improvement> improvementHolder = Optional.empty();
    private Optional<FieldEntity> entityHolder = Optional.empty();

    private Terrain terrain = Terrain.Normal;

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public boolean isPresent() {
        return entityHolder.isPresent();
    }

    public FieldEntity getEntity() {
        return entityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
    }

    public void clearField() {
        if (entityHolder.isPresent()) {
            entityHolder = Optional.empty();
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Improvement getImprovement() {
        return improvementHolder.get();
    }

    public void setImprovement(Improvement improvement) {
        improvementHolder = Optional.of(checkNotNull(improvement,
                "Improvment cannot be null."));
    }

    public void clearImprovement() {
        if (improvementHolder.isPresent()) {
            improvementHolder = Optional.empty();
        }
    }

    public boolean isImproved() {
        return improvementHolder.isPresent();
    }
}
