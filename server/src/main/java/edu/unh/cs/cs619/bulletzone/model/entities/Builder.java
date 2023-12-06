package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;

/**
 * Builder token. Spawned in with item, can do normal token actions as well as "hit"
 * other tokens and can "build" and "dismantle" improvements on the board
 * @author Anthony Papetti
 */
public class Builder extends PlayerToken {

    /**
     * Constructor. Handles values not set in PlayerToken.
     * @param id The ID of the tank
     * @param player The player object this token is associated with
     * @param ip IP of the player
     * @param accountID ID of the account this token is associated with
     */
    public Builder(long id, Player player, String ip, int accountID) {
        super(id, player, ip, accountID);
        setLife(50);
        setAllowedNumberOfBullets(4);
        setAllowedMoveInterval(250);
        setAllowedFireInterval(500);
        setBulletTracker(new BulletTracker(this, 256));
    }

    /**
     * {@inheritDoc}. Cannot Move sideways. 250ms base speed, 50% longer for hilly
     * terrain. Can enter forests and water.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return {@inheritDoc}
     */
    @Override
    public boolean canMove(long millis, Direction direction) {
        //Can enter forest
        //Entering hilly terrain takes 50% longer
        Terrain nextTerrain = getParent().getNeighbor(direction).getTerrain();
        long lastMoveTime = getLastMoveTime();
        if (nextTerrain == Terrain.Hilly) {
            millis = millis - (getAllowedMoveInterval() / 2);
        }
        System.out.printf("System: %d, Against: %d\n", millis, lastMoveTime);
        if (millis < getLastMoveTime()) {
            return false;
        }
        //builder cannot move sideways
        if (getDirection() == Direction.Up || getDirection() == Direction.Down) {
            return direction == Direction.Up || direction == Direction.Down;
        } else {
            return direction == Direction.Right || direction == Direction.Left;
        }
    }

    /**
     * {@inheritDoc} 250ms base turn speed. Can only turn 90 degrees.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will turn
     * @return {@inheritDoc}
     */
    @Override
    public boolean canTurn(long millis, Direction direction) {
        if (millis < getLastMoveTime()) {
            return false;
        }
        //tank only turn 90 degrees
        switch (getDirection()) {
            case Up:
                if (direction == Direction.Down) {
                    return false;
                }
                break;
            case Down:
                if (direction == Direction.Up) {
                    return false;
                }
                break;
            case Left:
                if (direction == Direction.Right) {
                    return false;
                }
            case Right:
                if (direction == Direction.Left) {
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the tank will be turned
     */
    public void turn(long millis, Direction direction) {
        setLastMoveTime(millis + getAllowedMoveInterval());
        setDirection(direction);
    }

    @Override
    public FieldEntity copy() {
        return new Builder(getId(), getPlayer(), getIp(), accountID);
    }

    @Override
    public String toString() {
        return "B";
    }

    /**
     * {@inheritDoc}
     * @param other Token that has moved into this entity
     * @return {@inheritDoc}
     */
    public int movedIntoBy(PlayerToken other) {
        return 0;
    }

    @Override
    public int getIntValue() {
        return (int) (50000000 + 10000 * getId() + 10 * getLife() + Direction
                .toByte(getDirection()));
    }

    /**
     * {@inheritDoc} Decreases life. If life is below 0, destroys Builder.
     * @param damage Damage done by the bullet.
     * @param game Current game.
     * @return {@inheritDoc}
     */
    @Override
    public boolean hit(int damage, Game game) {
        EventHistory eventHistory = EventHistory.get_instance();
        int life = getLife() - damage;
        setLife(life);

        if (life <= 0) {
            //Remove Builder
            getParent().clearField();
            setParent(null);
            game.removeBuilder(getId());
            eventHistory.addEvent(new TokenLeaveEvent(getId(), getIntValue()));

            //Remove Soldier If exists
            Soldier s = getPlayer().getSoldier();
            if (s != null) {
                s.getParent().clearField();
                s.setParent(null);
                game.removeSoldier(s.getId());
                //Add soldier hit event
                eventHistory.addEvent(new TokenLeaveEvent(s.getId(), s.getIntValue()));
            }

            //Remove Tank
            Tank t = getPlayer().getTank();
            t.getParent().clearField();
            t.setParent(null);
            game.removeTank(t.getId());
            //Add soldier hit event
            eventHistory.addEvent(new TokenLeaveEvent(t.getId(), t.getIntValue()));
            return true;
        }
        return false;
    }
}
