package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;
import edu.unh.cs.cs619.bulletzone.model.improvements.Improvement;

/**
 * Soldier token. Can be token actions such as move, fire, and turn. Can also re-enter a
 * tank by moving into it.
 * @author Anthony Papetti
 */
public class Soldier extends PlayerToken{

    /**
     * Constructor. Handles values not set in PlayerToken.
     * @param id The ID of the soldier
     * @param player The player object this token is associated with
     * @param ip IP of the player
     * @param accountID ID of the account this token is associated with
     */
    public Soldier(long id, Player player, String ip, int accountID) {
        super(id, player, ip, accountID);
        setLife(25);
        setAllowedNumberOfBullets(6);
        setAllowedMoveInterval(1000);
        setAllowedFireInterval(250);
        setBulletTracker(new BulletTracker(this, 256));
    }

    /**
     * {@inheritDoc} No constraints for Soldiers.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will turn
     * @return {@inheritDoc}
     */
    public boolean canTurn(long millis, Direction direction) {
        return true;
    }

    /**
     * {@inheritDoc}
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the tank will be turned
     */
    public void turn(long millis, Direction direction) {
        setDirection(direction);
    }

    /**
     * {@inheritDoc} 1000 ms base speed. Entering rocky terrian takes 50% longer.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return {@inheritDoc}
     */
    @Override
    public boolean canMove(long millis, Direction direction) {
        //Entering rocky terrain takes 50% longer
        //Cannot enter water
        FieldHolder nextField = getParent().getNeighbor(direction);
        Terrain nextTerrain = nextField.getTerrain();
        Improvement i = null;
        if (nextField.isImproved()) {
            i = nextField.getImprovement();
            millis = i.mutateTime(millis, getAllowedMoveInterval());
        }
        if (nextTerrain == Terrain.Rocky) {
            millis = millis - (getAllowedMoveInterval() / 2);
        } else if (nextTerrain == Terrain.Water) {
            if (i != null) {
                if (!i.isDock()) {
                    return false;
                }
            }else {
                return false;
            }
        }
        return millis >= getLastMoveTime();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public FieldEntity copy() {
        return new Soldier(getId(), getPlayer(), getIp(), getAccountID());
    }

    @Override
    public String toString() {
        return "S";
    }

    /**
     * Creates int value from soldier that is used as a representation in the grid.
     * @return Int representation of soldier
     */
    @Override
    public int getIntValue() {
        return (int) (30000000 + 10000 * getId() + 10 * getLife() + Direction
                .toByte(getDirection()));
    }

    /**
     * {@inheritDoc}
     * @param token Token that has moved into this entity
     * @return 0: Move was not successful
     */
    @Override
    public int movedIntoBy(PlayerToken token) {
        return 0;
    }

    /**
     * {@inheritDoc} Decreases life. If life is below 0, destroys Soldier and it's
     * pair (Tank).
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
            //Remove Soldier
            getParent().clearField();
            setParent(null);
            game.removeSoldier(getId());
            eventHistory.addEvent(new TokenLeaveEvent(getId(), getIntValue()));

            //Remove Tank
            Tank t = getPlayer().getTank();
            t.getParent().clearField();
            t.setParent(null);
            game.removeTank(t.getId());
            //Add soldier hit event
            eventHistory.addEvent(new TokenLeaveEvent(t.getId(), t.getIntValue()));

            //Remove builder
            Builder builder = getPlayer().getBuilder();
            if (builder != null) {
                builder.getParent().clearField();
                builder.setParent(null);
                game.removeBuilder(builder.getId());
                eventHistory.addEvent(new TokenLeaveEvent(builder.getId(), builder.getIntValue()));
            }
            return true;
        }
        return false;
    }
}
