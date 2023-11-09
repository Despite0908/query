package edu.unh.cs.cs619.bulletzone.model;

import java.util.Timer;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;

public class BulletTracker {
    private final int[] bulletDamage ={10,30,50,5};
    private int []trackActiveBullets;

    private PlayerToken token;

    /**
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    private final Timer timer = new Timer();


    /**
     * Constructor. Sets token whose bullets this is tracking.
     * @param t Token
     */
    public BulletTracker(PlayerToken t, int bullets) {
        this.token = t;
        trackActiveBullets = new int[bullets];
    }

    /**
     * Getter for active bullets array.
     * @return Array for active bullets
     */
    public int[] getTrackActiveBullets() {
        return trackActiveBullets;
    }

    /**
     * Routine to fire bullet and set a BulletTimer to move bullet every BULLET_PERIOD.
     * Bullet Types 1-3 are for tanks, Bullet type 4 is for soldiers.
     * @param bulletType Type of bullet to be fired
     * @param game Game to fire the bullet on
     * @param monitor Object to synchronize firing
     */
    public void fire(int bulletType, Game game, Object monitor) {
        Direction direction = token.getDirection();
        FieldHolder parent = token.getParent();

        if(!(bulletType>=1 && bulletType<=3)) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }

        //Get ID of bullet
        int bulletId=0;
        boolean bulletFree = false;
        for (int i = 0; i < trackActiveBullets.length; i++) {
            if (trackActiveBullets[i] == 0) {
                bulletId = i;
                trackActiveBullets[i] = 1;
                bulletFree = true;
                break;
            }
        }
        if (!bulletFree) {
            System.out.println("Bullet Not Free");
            return;
        }
        token.setNumberOfBullets(token.getNumberOfBullets() + 1);

        // Create a new bullet to fire
        final Bullet bullet = new Bullet(token.getId(), direction, bulletDamage[bulletType-1]);
        // Set the same parent for the bullet.
        // This should be only a one way reference.
        bullet.setParent(parent);
        bullet.setBulletId(bulletId);
        timer.schedule(new BulletTimer(monitor, token, bullet, game), 0, BULLET_PERIOD);
    }
}