package edu.unh.cs.cs619.bulletzone.model;

import java.util.Timer;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;

public class BulletTracker {
    private final int[] bulletDamage ={10,30,50};
    private final int[] bulletDelay ={500,1000,1500};
    private int trackActiveBullets[]={0,0};

    private Tank tank;

    private Game game;

    private static final int BULLET_PERIOD = 200;

    private final Timer timer = new Timer();


    /**
     * Constructor. Sets tank whose bullets this is tracking.
     * @param t Tank
     */
    public BulletTracker(Tank t) {
        this.tank = t;
    }

    /**
     * Getter for active bullets array.
     * @return Array for active bullets
     */
    public int[] getTrackActiveBullets() {
        return trackActiveBullets;
    }

    /**
     * Routine to fire bullet and set a BulletTimer to move bullet every BULLET_PERIOD
     * @param bulletType Type of bullet to be fired
     * @param game Game to fire the bullet on
     * @param monitor Object to synchronize firing
     * @param eventHistory Event History to add on to
     */
    public void fire(int bulletType, Game game, Object monitor, EventHistory eventHistory) {
        Direction direction = tank.getDirection();
        FieldHolder parent = tank.getParent();
        tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

        if(!(bulletType>=1 && bulletType<=3)) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }

        int bulletId=0;
        if(trackActiveBullets[0]==0){
            trackActiveBullets[0] = 1;
        }else if(trackActiveBullets[1]==0){
            bulletId = 1;
            trackActiveBullets[1] = 1;
        }

        // Create a new bullet to fire
        final Bullet bullet = new Bullet(tank.getId(), direction, bulletDamage[bulletType-1]);
        // Set the same parent for the bullet.
        // This should be only a one way reference.
        bullet.setParent(parent);
        bullet.setBulletId(bulletId);
        timer.schedule(new BulletTimer(monitor, tank, bullet, game, eventHistory), 0, BULLET_PERIOD);
    }
}