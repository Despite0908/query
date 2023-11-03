package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameBuilder;
import edu.unh.cs.cs619.bulletzone.model.GameMap;
import edu.unh.cs.cs619.bulletzone.model.PlayerToken;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.MapLoader;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BoardCreationEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.FireEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.exceptions.TokenDoesNotExistException;

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
    private int bulletDelay[]={500,1000,1500,500};

    private int[] tankSpawn = null;

    private final EventHistory eventHistory = new EventHistory();

    private String mapPath = "Maps/DefaultMap.json";

    /**
     * Sets the map to load game from.
     * @param map Only file name needed, not path
     */
    public void setMapPath(String map) {
        String mapPrefix = "Maps/";
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
     * Checks constraints and turns a token.
     * @param tokenId Token to be turned
     * @param direction Direction to turn the token
     * @return Returns false if constraints are violated. Returns true if turn is successful.
     * @throws TokenDoesNotExistException Throws if there is no token corresponding to tokenID.
     * @throws IllegalTransitionException Throws if a token tries to turn in an illegal manner.
     * @throws LimitExceededException I honestly don't know on this one.
     */
    @Override
    public boolean turn(long tokenId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);
            // Find token
            PlayerToken token = game.getTanks().get(tokenId);
            if (token == null) {
                token = game.getSoldiers().get(tokenId);
                if (token == null) {
                    throw new TokenDoesNotExistException(tokenId);
                }
            }

            long millis = System.currentTimeMillis();
            //Constraint checking
            if (!token.canTurn(millis, direction)) {
                return false;
            }

            //Turn token
            token.turn(millis, direction);

            //Add event to history
            eventHistory.addEvent(new TokenMoveEvent(token.getId(), direction, token.getIntValue(), getGrid()));

            return true;
        }
    }

    /**
     * Checks constraints and moves a token
     * @param tokenId Token to be moved
     * @param direction direction to move token in
     * @return Returns false if constraints are violated. Returns true if move is successful.
     * @throws TokenDoesNotExistException Throws if there is no thank corresponding to tokenID.
     * @throws IllegalTransitionException I'm not sure here because this exception is specifically about turns.
     * @throws LimitExceededException Don't know here
     */
    @Override
    public boolean move(long tokenId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);
            // Find token
            PlayerToken token = game.getTanks().get(tokenId);
            if (token == null) {
                token = game.getSoldiers().get(tokenId);
                if (token == null) {
                    throw new TokenDoesNotExistException(tokenId);
                }
            }

            //Token constraints
            long millis = System.currentTimeMillis();
            if (!token.canMove(millis, direction)) {
                return false;
            }

            //move tank and set event
            if (token.move(millis, direction)) {
                //Add move event
                eventHistory.addEvent(new TokenMoveEvent(token.getId(), direction, token.getIntValue(), getGrid()));
                return true;
            }
            return false;
        }
    }

    /**
     * Checks constraints, fires bullet, then sets timer to move bullet.
     * @param tokenId Token to fire bullet from
     * @param bulletType Type of bullet to be fired.
     * @return Returns true if bullet fired successfully. Returns false if constraint violated.
     * @throws TokenDoesNotExistException Throws if there is no thank corresponding to tokenID.
     * @throws LimitExceededException Don't know on this one
     */
    @Override
    public boolean fire(long tokenId, int bulletType)
            throws TokenDoesNotExistException, LimitExceededException {
        synchronized (this.monitor) {

            // Find token
            PlayerToken token = game.getTanks().get(tokenId);
            if (token == null) {
                throw new TokenDoesNotExistException(tokenId);
            }

            long millis = System.currentTimeMillis();
            //Constraint Checking
            if (!token.canFire(millis)) {
                return false;
            }
            //Set new timestamp
            token.setLastFireTime(millis + bulletDelay[bulletType - 1] + token.getAllowedFireInterval());

            //fire bullet//add fire event
            token.getBulletTracker().fire(bulletType, game, monitor, eventHistory);
            eventHistory.addEvent(new FireEvent(token.getId()));
            return true;
        }
    }

    /**
     * Removes tank
     * @param tankId Tank to be removed
     * @throws TokenDoesNotExistException Throws if there is no thank corresponding to tankID.
     */
    @Override
    public void leave(long tankId)
            throws TokenDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TokenDoesNotExistException(tankId);
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
