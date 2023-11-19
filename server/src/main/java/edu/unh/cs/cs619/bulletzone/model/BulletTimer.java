package edu.unh.cs.cs619.bulletzone.model;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletHitEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.entities.Bullet;
import edu.unh.cs.cs619.bulletzone.model.entities.Item;
import edu.unh.cs.cs619.bulletzone.model.entities.PlayerToken;
import edu.unh.cs.cs619.bulletzone.model.entities.Soldier;
import edu.unh.cs.cs619.bulletzone.model.entities.Tank;
import edu.unh.cs.cs619.bulletzone.model.improvements.Improvement;

/**
 * Timer Task for Updating the positions of bullets
 * @author Anthony Papetti
 */
public class BulletTimer extends TimerTask {
    Object monitor;
    PlayerToken token;
    Bullet bullet;

    Game game;

    /**
     * Constructor. Passes values for task.
     * @param monitor Monitor to synchronize task
     * @param token Token bullet is fired from
     * @param bullet Bullet to move every step
     * @param game Game the bullet is from
     */
    public BulletTimer(Object monitor, PlayerToken token, Bullet bullet, Game game) {
        super();
        this.monitor = monitor;
        this.token = token;
        this.bullet = bullet;
        this.game = game;
    }

    /**
     * Task to be run every timer step. Handles moving bullet and hitting Field Entities
     */
    @Override
    public void run() {
        synchronized (monitor) {
            EventHistory eventHistory = EventHistory.get_instance();
            System.out.println("Active Bullet: "+ token.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
            FieldHolder currentField = bullet.getParent();
            Direction direction = bullet.getDirection();
            FieldHolder nextField = currentField
                    .getNeighbor(direction);

            // Is the bullet visible on the field?
            boolean isVisible = currentField.isPresent()
                    && (currentField.getEntity() == bullet);

            //Check for Forest
            if (nextField.getTerrain() == Terrain.Forest) {
                //TODO: Nick change this to a Token Leave Event
                eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, -1));
                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }
                token.getBulletTracker().getTrackActiveBullets()[bullet.getBulletId()] = null;
                token.setNumberOfBullets(token.getNumberOfBullets()-1);
                cancel();
            }

            //Checking for Walls
            //TODO: Nick Find a way to remove walls on events
            if (nextField.isImproved() && nextField.getImprovement().isSolid()) {
                Improvement wall = nextField.getImprovement();
                if (wall.getIntValue() >1000 && wall.getIntValue()<=2000 ){
                    nextField.clearImprovement();
                    //Add wall hit event
                    eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, wall.getIntValue()));
                } else {
                    //Add wall hit event
                    eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, wall.getIntValue()));
                }
                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }
                token.getBulletTracker().getTrackActiveBullets()[bullet.getBulletId()] = null;
                token.setNumberOfBullets(token.getNumberOfBullets()-1);
                eventHistory.addEvent(new TokenLeaveEvent(bullet.getId(), bullet.getIntValue()));
                cancel();
            } else if (nextField.isPresent()) {
                nextField.getEntity().hit(bullet.getDamage(), game);

                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }
                token.getBulletTracker().getTrackActiveBullets()[bullet.getBulletId()] = null;
                token.setNumberOfBullets(token.getNumberOfBullets()-1);
                eventHistory.addEvent(new TokenLeaveEvent(bullet.getId(), bullet.getIntValue()));
                cancel();

            } else {
                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }

                nextField.setFieldEntity(bullet);
                bullet.setParent(nextField);
                eventHistory.addEvent(new BulletMoveEvent(token.getId(), bullet.getDirection(), bullet.getIntValue(), game.getGrid2D()));
            }
        }
    }
}
