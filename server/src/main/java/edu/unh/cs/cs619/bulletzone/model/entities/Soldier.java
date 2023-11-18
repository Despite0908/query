package edu.unh.cs.cs619.bulletzone.model.entities;

import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;

public class Soldier extends PlayerToken{

    /**
     * Constructor. Handles values not set in PlayerToken.
     * @param id The ID of the soldier
     * @param direction The initial direction of the soldier
     * @param ip IP of the player
     */
    public Soldier(long id, Direction direction, String ip) {
        super(id, direction, ip);
        setLife(25);
        setAllowedNumberOfBullets(6);
        setAllowedMoveInterval(1000);
        setBulletTracker(new BulletTracker(this, 6));
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return {@inheritDoc}
     */
    @Override
    public boolean canMove(long millis, Direction direction) {
        //Entering rocky terrain takes 50% longer
        Terrain nextTerrain = getParent().getNeighbor(direction).getTerrain();
        if (nextTerrain == Terrain.Rocky) {
            millis = millis - (getAllowedMoveInterval() / 2);
        }
        return millis >= getLastMoveTime();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public FieldEntity copy() {
        return new Soldier(getId(), getDirection(), getIp());
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
     * {@inheritDoc}
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
            Tank t = (Tank) getPair();
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
