package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.entities.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;

/**
 * @author Bence Cserna (bence@cserna.net)
 */

public class NumberField extends FieldEntity {

    private static final String TAG = "NumberField";
    private final int value;

    public NumberField(long id, int value) {
        super(id);
        this.value = value;
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public FieldEntity copy() {
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(value == 1000 ? 1 : 2);
    }

    public int movedIntoBy(PlayerToken other) {
        return 0;
    }

}
