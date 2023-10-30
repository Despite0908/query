package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameBuilder;
import edu.unh.cs.cs619.bulletzone.model.GameMap;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.MapLoader;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BoardCreationEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletHitEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.FireEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TankMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class that changes the model based on requests passed from client. Only directly game-related
 * functions are handled here.
 */

@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private int bulletDelay[]={500,1000,1500};

    private int[] tankSpawn = null;

    private final EventHistory eventHistory = new EventHistory();

    private String mapPath = "src/main/Maps/DefaultMap.json";

    /**
     * Sets the map to load game from.
     * @param map Only file name needed, not path
     */
    public void setMapPath(String map) {
        String mapPrefix = "src/main/Maps/";
        mapPath = mapPrefix + map;
    }

    /**
     * USED FOR TESTING PURPOSES ONLY. Sets tank spawn to a specific cell.
     * @param x Row for tank to spawn in
     * @param y Column for tank to spawn in.
     */
    public void setTankSpawn(int x, int y) {
        if (x >= FIELD_DIM || y >= FIELD_DIM) {
            return;
        }
        if (tankSpawn == null) {
            tankSpawn = new int[]{x, y};
        } else {
            tankSpawn[0] = x;
            tankSpawn[1] = y;
        }
    }

    /**
     * Adds a player to the current game. If there is no game occurring, it creates one.
     * @param ip IP address of the player joining.
     * @return Returns the Tank object for the tank added to the game.
     */
    @Override
    public Tank join(String ip) {
        synchronized (this.monitor) {
            Tank tank;
            if (game == null) {
                this.create();
            }

            //If tank is already in the game
            if( (tank = game.getTank(ip)) != null){
                return tank;
            }

            Long tankId = this.idGenerator.getAndIncrement();

            tank = new Tank(tankId, Direction.Up, ip);
            tank.setLife(TANK_LIFE);

            Random random = new Random();

            // This may run for forever.. If there is no free space. XXX
            boolean firstTry = true;
            for (; ; ) {
                if (firstTry && tankSpawn == null) {
                    tankSpawn = new int[2];
                    tankSpawn[0] = random.nextInt(FIELD_DIM);
                    tankSpawn[1] = random.nextInt(FIELD_DIM);
                } else if (!firstTry && tankSpawn != null) {
                    tankSpawn[0] = random.nextInt(FIELD_DIM);
                    tankSpawn[1] = random.nextInt(FIELD_DIM);
                }
                FieldHolder fieldElement = game.getHolderGrid().get(tankSpawn[0] * FIELD_DIM + tankSpawn[1]);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(tank);
                    tank.setParent(fieldElement);
                    break;
                }
                firstTry = false;
            }

            game.addTank(ip, tank);
            eventHistory.addEvent(new AddTankEvent(tank.getId()));
            return tank;
        }
    }

    /**
     * Returns an integer array representation of the field grid. If there is no
     * current game, it creates one.
     * @return An array representation of the field grid.
     */
    @Override
    public int[][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

    /**
     * Checks constraints and turns a tank.
     * @param tankId Tank to be turned
     * @param direction Direction to turn the tank
     * @return Returns false if constraints are violated. Returns true if turn is successful.
     * @throws TankDoesNotExistException Throws if there is no thank corresponding to tankID.
     * @throws IllegalTransitionException Throws if a tank tries to turn in an illegal manner.
     * @throws LimitExceededException I honestly don't know on this one.
     */
    @Override
    public boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                throw new TankDoesNotExistException(tankId);
            }

            long millis = System.currentTimeMillis();
            //Constraint checking
            if (!tank.turnConstraints(millis, direction)) {
                return false;
            }

            //Set new Timestamp
            tank.setLastMoveTime(millis+tank.getAllowedMoveInterval());

            tank.setDirection(direction);

            //Add event
            eventHistory.addEvent(new TankMoveEvent(tank.getId(), direction, tank.getIntValue(), getGrid()));

            return true;
        }
    }

    /**
     * Checks constraints and moves a tank
     * @param tankId Tank to be moved
     * @param direction direction to move tank in
     * @return Returns false if constraints are violated. Returns true if move is successful.
     * @throws TankDoesNotExistException Throws if there is no thank corresponding to tankID.
     * @throws IllegalTransitionException I'm not sure here because this exception is specifically about turns.
     * @throws LimitExceededException Don't know here
     */
    @Override
    public boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            // Find tank

            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }

            //Make sure tank can only move every 0.5 seconds
            long millis = System.currentTimeMillis();
            if (!tank.moveConstraints(millis, direction)) {
                return false;
            }

            //Set new timestamp
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            //Move the tank from parent to nextField
            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

            boolean isCompleted;
            if (!nextField.isPresent()) {
                // If the next field is empty move the user
                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);

                isCompleted = true;
                //Add move event
                eventHistory.addEvent(new TankMoveEvent(tank.getId(), direction, tank.getIntValue(), getGrid()));
            } else {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

    /**
     * Checks constraints, fires bullet, then sets timer to move bullet.
     * @param tankId Tank to fire bullet from
     * @param bulletType Type of bullet to be fired.
     * @return Returns true if bullet fired successfully. Returns false if constraint violated.
     * @throws TankDoesNotExistException Throws if there is no thank corresponding to tankID.
     * @throws LimitExceededException Don't know on this one
     */
    @Override
    public boolean fire(long tankId, int bulletType)
            throws TankDoesNotExistException, LimitExceededException {
        synchronized (this.monitor) {

            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                //Log.i(TAG, "Cannot find user with id: " + tankId);
                //return false;
                throw new TankDoesNotExistException(tankId);
            }

            long millis = System.currentTimeMillis();
            //Constraint Checking
            if (!tank.fireConstraints(millis)) {
                return false;
            }
            //Set new timestamp
            tank.setLastFireTime(millis + bulletDelay[bulletType - 1] + tank.getAllowedFireInterval());

            //fire bullet//add fire event
            tank.getBulletTracker().fire(bulletType, game, monitor, eventHistory);
            eventHistory.addEvent(new FireEvent(tank.getId()));
            return true;
        }
    }

    /**
     * Removes tank
     * @param tankId Tank to be removed
     * @throws TankDoesNotExistException Throws if there is no thank corresponding to tankID.
     */
    @Override
    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            Tank tank = game.getTanks().get(tankId);
            FieldHolder parent = tank.getParent();
            parent.clearField();
            game.removeTank(tankId);
        }
    }

    /**
     * Creates a game according to mapPath
     */
    public void create() {
        if (game != null) {
            return;
        }
        synchronized (this.monitor) {

            //Load data from JSON file using loaders
            MapLoader mapLoader = new MapLoader(mapPath);
            GameMap gMap = mapLoader.load();
            List<GameMap.WallLoader> wallData = gMap.getWalls();
            GameBuilder gameBuilder = new GameBuilder();

            //Push data into builders
            for (int i = 0; i < wallData.size(); i++) {

                gameBuilder.setWall(wallData.get(i).getPos(), wallData.get(i).getDestructVal());
            }

            //Build map and holder grid
            this.game = gameBuilder.build();
            //Add creation event
            eventHistory.addEvent(new BoardCreationEvent());
        }
    }

    public String[] event(long millis) {
        synchronized (this.monitor) {
            List<GridEvent> events = eventHistory.getEventsAfter(millis);
            String[] JSONEvents = new String[events.size()];
            for (int i = 0; i < events.size(); i++) {
                JSONEvents[i] = events.get(i).toJSON();
            }
            return JSONEvents;
        }
    }

}
