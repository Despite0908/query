package edu.unh.cs.cs619.bulletzone.model;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletHitEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletMoveEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;

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

            if (nextField.getTerrain() == Terrain.Forest) {
                //TODO: Nick change this to a Token Leave Event
                eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, -1));
                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }
                token.getBulletTracker().getTrackActiveBullets()[bullet.getBulletId()]=0;
                token.setNumberOfBullets(token.getNumberOfBullets()-1);
                cancel();
            }

            if (nextField.isPresent()) {
                //TODO: RESOLVE BULLSHIT

                // Something is there, hit it
//                FieldEntity entity = nextField.getEntity();
//                boolean destroyed = entity.hit(bullet.getDamage());
//
//                if (destroyed) {
//                    entity.getParent().clearField();
//                    entity.setParent(null);
//                    game.removeTank(entity.getId());
//                    game.removeSoldiers(entity.getId());
//                    game.getItems().remove(entity.getId());
//                }
                if ( nextField.getEntity() instanceof  Tank){
                    Tank t = (Tank) nextField.getEntity();
                    System.out.println("tank is hit, tank life: " + t.getLife());
                    if (t.getLife() <= 0 ){
                        t.getParent().clearField();
                        t.setParent(null);
                        game.removeTank(t.getId());
                        //Add tank hit event
                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, t.getIntValue()));
                        Soldier s = (Soldier) t.getPair();
                        if (s != null) {
                            s.getParent().clearField();
                            s.setParent(null);
                            game.removeSoldier(s.getId());
                            //Add soldier hit event
                            eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, s.getIntValue()));
                        }
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
                else if ( nextField.getEntity() instanceof  Item){
                    Item i = (Item) nextField.getEntity();
                    game.getHolderGrid().get(i.getGridLocation()).clearField();
                    game.getItems().remove(i.getGridLocation());
                    //TODO aiden add to event list
                } else if(nextField.getEntity() instanceof  Soldier) {
                    Soldier s = (Soldier) nextField.getEntity();
                    System.out.println("tank is hit, tank life: " + s.getLife());
                    if (s.getLife() <= 0 ){
                        s.getParent().clearField();
                        s.setParent(null);
                        game.removeSoldier(s.getId());
                        //Add soldier hit event
                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, s.getIntValue()));
                        //Remove Tank
                        Tank t = (Tank) s.getPair();
                        t.getParent().clearField();
                        t.setParent(null);
                        game.removeTank(t.getId());
                        //add tank hit event
                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), true, t.getIntValue()));
                    } else {
                        //Add soldier hit event
                        eventHistory.addEvent(new BulletHitEvent(bullet.getIntValue(), false, s.getIntValue()));
                    }
                }
                if (isVisible) {
                    // Remove bullet from field
                    currentField.clearField();
                }
                token.getBulletTracker().getTrackActiveBullets()[bullet.getBulletId()]=0;
                token.setNumberOfBullets(token.getNumberOfBullets()-1);
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
