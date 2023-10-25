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
import edu.unh.cs.cs619.bulletzone.model.GameConstraints;
import edu.unh.cs.cs619.bulletzone.model.GameMap;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.MapLoader;
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
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private final Timer timer = new Timer();
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;
    private int bulletDamage[]={10,30,50};
    private int bulletDelay[]={500,1000,1500};
    private int trackActiveBullets[]={0,0};

    private int tankSpawn[] = null;

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

            GameConstraints constraints = new GameConstraints(tank);
            long millis = System.currentTimeMillis();
            //Constraint checking
            if (!constraints.checkMoveInterval(millis)) {
                return false;
            }
            if (!constraints.checkTurnConstraints(direction)) {
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

            GameConstraints constraints = new GameConstraints(tank);

            //Make sure tank can only move every 0.5 seconds
            long millis = System.currentTimeMillis();
            if (!constraints.checkMoveInterval(millis)) {
                return false;
            }
            if (!constraints.checkMoveConstraints(direction)) {
                return false;
            }

            //Set new timestamp
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            //Move the tank from parent to nextField
            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");


            //TODO: POSSIBLY FACTOR OUT TO GameConstraints??
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

            GameConstraints constraints = new GameConstraints(tank);

            if(constraints.checkBulletsFull())
                return false;

            long millis = System.currentTimeMillis();
            if (!constraints.checkFireInterval(millis)) {
                return false;
            }

            //Set new timestamp
            tank.setLastFireTime(millis + bulletDelay[bulletType - 1] + tank.getAllowedFireInterval());

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

            if(!(bulletType>=1 && bulletType<=3)) {
                System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
                bulletType = 1;
            }

            int bulletId=0;
            if(trackActiveBullets[0]==0){
                bulletId = 0;
                trackActiveBullets[0] = 1;
            }else if(trackActiveBullets[1]==0){
                bulletId = 1;
                trackActiveBullets[1] = 1;
            }

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, bulletDamage[bulletType-1]);
            // Set the same parent for the bullet.
            // This should be only a one way reference.
            bullet.setParent(parent);
            bullet.setBulletId(bulletId);

            // TODO make it nicer
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    synchronized (monitor) {
                        System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField
                                .getNeighbor(direction);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);


                            if (nextField.isPresent()) {
                                // Something is there, hit it
                                nextField.getEntity().hit(bullet.getDamage());

                                if ( nextField.getEntity() instanceof  Tank){
                                    Tank t = (Tank) nextField.getEntity();
                                    System.out.println("tank is hit, tank life: " + t.getLife());
                                    if (t.getLife() <= 0 ){
                                        t.getParent().clearField();
                                        t.setParent(null);
                                        game.removeTank(t.getId());
                                        //Add tank hit event
                                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, t.getIntValue()));
                                    } else {
                                        //Add tank hit event
                                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, t.getIntValue()));
                                    }
                                }
                                else if ( nextField.getEntity() instanceof  Wall){
                                    Wall w = (Wall) nextField.getEntity();
                                    if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                        game.getHolderGrid().get(w.getPos()).clearField();
                                        //Add wall hit event
                                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, w.getIntValue()));
                                    } else {
                                        //Add wall hit event
                                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, w.getIntValue()));
                                    }
                                }
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }
                            trackActiveBullets[bullet.getBulletId()]=0;
                            tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                            cancel();

                        } else {
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }

                            nextField.setFieldEntity(bullet);
                            bullet.setParent(nextField);
                            eventHistory.addEvent(new BulletMoveEvent(tankId, bullet.getDirection(), bullet.getIntValue(), getGrid()));
                        }
                    }
                }
            }, 0, BULLET_PERIOD);
            //Add fire event
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
