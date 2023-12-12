package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;

public interface GameRepository {

    Player join(String ip, int id);

    int[][] getGrid();

    public int[][] getTerrainGrid();

    boolean turn(long tankId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException;

    long move(long tankId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, int strength)
            throws TokenDoesNotExistException, LimitExceededException;

    public boolean build(long builderId, byte improvementType, boolean debugBuild);

    public boolean dismantle(long builderId, boolean debugBuild);

    public void leave(long tankId)
            throws TokenDoesNotExistException;

    String[] event(long millis);

    public Soldier eject(long tankId) throws TokenDoesNotExistException;

    public int[] getInventory(int id);

    int getTankHealth(long tankId) throws TokenDoesNotExistException;
    int getTankShieldHealth(long tankId) throws TokenDoesNotExistException;
    int getSoldierHealth(long soldierId) throws TokenDoesNotExistException;
    int getBuilderHealth(long builderId) throws TokenDoesNotExistException;
}
