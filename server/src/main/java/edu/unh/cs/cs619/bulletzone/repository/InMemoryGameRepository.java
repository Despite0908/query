package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameBuilder;
import edu.unh.cs.cs619.bulletzone.model.ItemSpawnTimer;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.MapLoader;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.AddTokenEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BoardCreationEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.FireEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
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
    private int bulletDelay[]={500,1000,1500,250};

    private int[] tankSpawn = null;

    private Clock c = Clock.systemUTC();
    private final EventHistory eventHistory = EventHistory.start(c);

    private String mapPath = "Maps/DefaultMap.json";

    // adding item spawn timer
    private static final int ITEM_PERIOD = 1000;
    private final Timer itemTimer = new Timer();

    /**
     * ONLY USED FOR TESTING. Changes the system clock.
     * @param c Clock to be injected
     */
    public void injectClock(Clock c) {
        this.c = c;
    }

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
    public Tank join(String ip, int accountID) {
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

            tank = new Tank(tankId, Direction.Up, ip, accountID);
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
            eventHistory.addEvent(new AddTokenEvent(tank.getIntValue(), tankSpawn[0] * FIELD_DIM + tankSpawn[1]));
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
     * Compiles a 2D array of terrain from the field.
     * @return A 2D integer array representing field terrain.
     */
    public int[][] getTerrainGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getTerrainGrid();
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
                    return false;
                }
            }

            long millis = c.millis();
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
     * Spawns a soldier and pairs it with the current tank.
     * @param tankId Tank soldier is paired with
     * @return Returns the soldier that is spawned
     * @throws TokenDoesNotExistException
     */
    public Soldier eject(long tankId) throws TokenDoesNotExistException {
        synchronized (this.monitor) {
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                return null;
            }
            //If there has already been an ejection
            if (tank.isEjected()) {
                return null;
            }
            //Look for open position
            FieldHolder parent = tank.getParent();
            FieldHolder holder = null;
            for (Direction dir: Direction.values()) {
                FieldHolder neighbor = parent.getNeighbor(dir);
                if (!neighbor.isPresent() && !(neighbor.isImproved() && neighbor.getImprovement().isSolid())) {
                    holder = neighbor;
                    break;
                }
            }
            //If no open position
            if (holder == null) {
                return null;
            }
            //Spawn Soldier
            Soldier soldier;
            if (tank.getPair() == null) {
                soldier = new Soldier(idGenerator.getAndIncrement(), Direction.Up, tank.getIp(), tank.getAccountID());
            } else {
                soldier = (Soldier) tank.getPair();
            }
            soldier.setParent(holder);
            holder.setFieldEntity(soldier);
            //Pair solder/tank
            soldier.setPair(tank);
            tank.setPair(soldier);
            tank.setEjected(true);
            //Add to game
            game.addSoldier(soldier);
            //Add event
            eventHistory.addEvent(new AddTokenEvent(soldier.getIntValue(), game.getHolderGrid().indexOf(parent)));
            //Return soldier
            return soldier;
        }
    }

    /**
     * Checks constraints and moves a token
     * @param tokenId Token to be moved
     * @param direction direction to move token in
     * @return TEMPORARY: RETURNS LONG FROM MOVERESULT. RETURN TO ORIGINAL LATER. Returns false if constraints are violated. Returns true if move is successful.
     * @throws TokenDoesNotExistException Throws if there is no thank corresponding to tokenID.
     * @throws IllegalTransitionException I'm not sure here because this exception is specifically about turns.
     * @throws LimitExceededException Don't know here
     */
    @Override
    public long move(long tokenId, Direction direction)
            throws TokenDoesNotExistException, IllegalTransitionException, LimitExceededException {
        synchronized (this.monitor) {
            checkNotNull(direction);
            // Find token
            PlayerToken token = game.getTanks().get(tokenId);
            if (token == null) {
                token = game.getSoldiers().get(tokenId);
                if (token == null) {
                    return 0;
                }
            }

            //Token constraints
            long millis = c.millis();
            if (!token.canMove(millis, direction)) {
                return 0;
            }

            int moveResult = token.move(millis, direction);
            //move tank and set event
            if (moveResult == 1) {
                //Add move event
                eventHistory.addEvent(new TokenMoveEvent(token.getId(), direction, token.getIntValue(), getGrid()));
                return 1;
            } else if (moveResult == 2) {
                game.removeSoldier(tokenId);
                eventHistory.addEvent(new TokenLeaveEvent(token.getId(), token.getIntValue()));
                return 2;
            }
            return 0;
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
                token = game.getSoldiers().get(tokenId);
                if (token == null) {
                    return false;
                }
            }

            long millis = c.millis();
            //Constraint Checking
            if (!token.canFire(millis)) {
                return false;
            }
            //Set new timestamp
            token.setLastFireTime(millis + token.getAllowedFireInterval());

            //fire bullet//add fire event
            token.getBulletTracker().fire(bulletType, game, monitor);
            token.cleanPair();
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
            //Remove soldier if it exists
            Soldier soldier = (Soldier) tank.getPair();
            if (soldier != null) {
                soldier.getParent().clearField();
                soldier.setParent(null);
                game.removeSoldier(soldier.getId());
            }
            eventHistory.addEvent(new TokenLeaveEvent(tank.getId(), tank.getIntValue()));
        }
    }

    @Override
    public int[] getInventory(int id){
        return getInventory(id);
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
            GameBuilder gameBuilder = mapLoader.load();

            //Build map and holder grid
            this.game = gameBuilder.build();
            //Add creation event
            eventHistory.addEvent(new BoardCreationEvent());
            itemTimer.schedule(new ItemSpawnTimer(game, idGenerator, monitor), 0, ITEM_PERIOD);
        }
    }

    /**
     * Generates a list of JSON strings representing a list of events since millis.
     * @param millis A timestamp in milliseconds
     * @return Array of JSON events that happened since millis
     */
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
