package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.GameConstraints;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;

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
            int x;
            int y;

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(tank);
                    tank.setParent(fieldElement);
                    break;
                }
            }

            game.addTank(ip, tank);

            return tank;
        }
    }

    @Override
    public int[][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

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
            if (!GameConstraints.checkMoveInterval(tank, millis)) {
                return false;
            }
            if (!GameConstraints.checkTurnConstraints(tank, direction)) {
                return false;
            }

            //Set new Timestamp
            tank.setLastMoveTime(millis+tank.getAllowedMoveInterval());

            /*try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }*/

            tank.setDirection(direction);

            return true;
        }
    }

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
            if (!GameConstraints.checkMoveInterval(tank, millis)) {
                return false;
            }
            if (!GameConstraints.checkMoveConstraints(tank, direction)) {
                return false;
            }

            //Set new timestamp
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            //Move the tank from parent to nextField
            FieldHolder parent = tank.getParent();

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

            boolean isCompleted;
            if (!nextField.isPresent()) {
                // If the next field is empty move the user
                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);

                isCompleted = true;
            } else {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

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

            if(GameConstraints.checkBulletsFull(tank))
                return false;

            long millis = System.currentTimeMillis();
            GameConstraints.checkFireInterval(tank, millis);

            //Log.i(TAG, "Cannot find user with id: " + tankId);
            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

            if(!(bulletType>=1 && bulletType<=3)) {
                System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
                bulletType = 1;
            }

            //Set new timestamp
            tank.setLastFireTime(millis + bulletDelay[bulletType - 1] + tank.getAllowedFireInterval());

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
                                    }
                                }
                                else if ( nextField.getEntity() instanceof  Wall){
                                    Wall w = (Wall) nextField.getEntity();
                                    if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                        game.getHolderGrid().get(w.getPos()).clearField();
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
                        }
                    }
                }
            }, 0, BULLET_PERIOD);

            return true;
        }
    }

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

    public void create() {
        if (game != null) {
            return;
        }
        synchronized (this.monitor) {

            this.game = new Game();

            createFieldHolderGrid(game);

            // Test // TODO Move to more appropriate place (and if desired, integrate map loader)
            game.getHolderGrid().get(1).setFieldEntity(new Wall());
            game.getHolderGrid().get(2).setFieldEntity(new Wall());
            game.getHolderGrid().get(3).setFieldEntity(new Wall());

            game.getHolderGrid().get(17).setFieldEntity(new Wall());
            game.getHolderGrid().get(33).setFieldEntity(new Wall(1500, 33));
            game.getHolderGrid().get(49).setFieldEntity(new Wall(1500, 49));
            game.getHolderGrid().get(65).setFieldEntity(new Wall(1500, 65));

            game.getHolderGrid().get(34).setFieldEntity(new Wall());
            game.getHolderGrid().get(66).setFieldEntity(new Wall(1500, 66));

            game.getHolderGrid().get(35).setFieldEntity(new Wall());
            game.getHolderGrid().get(51).setFieldEntity(new Wall());
            game.getHolderGrid().get(67).setFieldEntity(new Wall(1500, 67));

            game.getHolderGrid().get(5).setFieldEntity(new Wall());
            game.getHolderGrid().get(21).setFieldEntity(new Wall());
            game.getHolderGrid().get(37).setFieldEntity(new Wall());
            game.getHolderGrid().get(53).setFieldEntity(new Wall());
            game.getHolderGrid().get(69).setFieldEntity(new Wall(1500, 69));

            game.getHolderGrid().get(7).setFieldEntity(new Wall());
            game.getHolderGrid().get(23).setFieldEntity(new Wall());
            game.getHolderGrid().get(39).setFieldEntity(new Wall());
            game.getHolderGrid().get(71).setFieldEntity(new Wall(1500, 71));

            game.getHolderGrid().get(8).setFieldEntity(new Wall());
            game.getHolderGrid().get(40).setFieldEntity(new Wall());
            game.getHolderGrid().get(72).setFieldEntity(new Wall(1500, 72));

            game.getHolderGrid().get(9).setFieldEntity(new Wall());
            game.getHolderGrid().get(25).setFieldEntity(new Wall());
            game.getHolderGrid().get(41).setFieldEntity(new Wall());
            game.getHolderGrid().get(57).setFieldEntity(new Wall());
            game.getHolderGrid().get(73).setFieldEntity(new Wall());
        }
    }

    private void createFieldHolderGrid(Game game) {
        synchronized (this.monitor) {
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

}
