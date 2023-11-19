package edu.unh.cs.cs619.bulletzone.model.entities;

import com.google.common.eventbus.EventBus;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.BulletHitEvent;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;

public class Tank extends PlayerToken {

    private static final String TAG = "Tank";
    EventBus eventBus = BusProvider.BusProvider().eventBus;

    /**
     * Constructor. Handles values not set in PlayerToken.
     * @param id The ID of the tank
     * @param direction The initial direction of the tank
     * @param ip IP of the player
     */
    public Tank(long id, Direction direction, String ip) {
        super(id, direction, ip);
        setLife(100);
        setAllowedNumberOfBullets(2);
        setAllowedMoveInterval(500);
        setBulletTracker(new BulletTracker(this, 256));
    }

    /**
     * {@inheritDoc}
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return {@inheritDoc}
     */
    @Override
    public boolean canMove(long millis, Direction direction) {
        //Cannot enter forest
        //Entering hilly terrain takes 50% longer
        Terrain nextTerrain = getParent().getNeighbor(direction).getTerrain();
        long lastMoveTime = getLastMoveTime();
        if (nextTerrain == Terrain.Forest) {
            return false;
        } else if (nextTerrain == Terrain.Hilly) {
            millis = millis - (getAllowedMoveInterval() / 2);
        }
        System.out.printf("System: %d, Against: %d\n", millis, lastMoveTime);
        if (millis < getLastMoveTime()) {
            return false;
        }
        //tank cannot move sideways
        if (getDirection() == Direction.Up || getDirection() == Direction.Down) {
            return direction == Direction.Up || direction == Direction.Down;
        } else {
            return direction == Direction.Right || direction == Direction.Left;
        }
    }

    /**
     * {@inheritDoc}
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
        return new Tank(getId(), getDirection(), getIp());
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
            //Remove Tank
			getParent().clearField();
            setParent(null);
            game.removeTank(getId());
            eventHistory.addEvent(new TokenLeaveEvent(getId(), getIntValue()));

            //Remove Soldier If exists
            Soldier s = (Soldier) getPair();
            if (s != null) {
                s.getParent().clearField();
                s.setParent(null);
                game.removeSoldier(s.getId());
                //Add soldier hit event
                eventHistory.addEvent(new TokenLeaveEvent(s.getId(), s.getIntValue()));
            }
            return true;
        }
        return false;
    }

    @Override
    public int getIntValue() {
        return (int) (10000000 + 10000 * getId() + 10 * getLife() + Direction
                .toByte(getDirection()));
    }

    @Override
    public String toString() {
        return "T";
    }

    /**
     * {@inheritDoc}
     * @param other Token that has moved into this entity
     * @return {@inheritDoc}
     */
    public int movedIntoBy(PlayerToken other) {
        if (other == pair) {
            //Remove from tank and field holder
            other.getParent().clearField();
            other.setParent(null);
            setPair(null);
            return 2;
        }
        return 0;
    }

}
