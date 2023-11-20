package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;

public interface GameRepository {

    Tank join(String ip);

    int[][] getGrid();

    public int[][] getTerrainGrid();

    boolean turn(long tankId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException;

    long move(long tankId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, int strength)
            throws TokenDoesNotExistException, LimitExceededException;

    public void leave(long tankId)
            throws TokenDoesNotExistException;

    String[] event(long millis);

    public Soldier eject(long tankId) throws TokenDoesNotExistException;

    public int[] getInventory(String username);


}
