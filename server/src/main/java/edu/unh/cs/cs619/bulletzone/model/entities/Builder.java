package edu.unh.cs.cs619.bulletzone.model.entities;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Player;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.EventHistory;
import edu.unh.cs.cs619.bulletzone.model.ServerEvents.TokenLeaveEvent;
import edu.unh.cs.cs619.bulletzone.model.Terrain;
import edu.unh.cs.cs619.bulletzone.model.improvements.Deck;
import edu.unh.cs.cs619.bulletzone.model.improvements.Improvement;
import edu.unh.cs.cs619.bulletzone.model.improvements.ImprovementMapper;
import edu.unh.cs.cs619.bulletzone.model.improvements.Road;
import edu.unh.cs.cs619.bulletzone.model.improvements.Wall;

/**
 * Builder token. Spawned in with item, can do normal token actions as well as "hit"
 * other tokens and can "build" and "dismantle" improvements on the board
 * @author Anthony Papetti
 */
public class Builder extends PlayerToken {

    private boolean isBuilding, isDismantling;

    private Timer buildTimer;

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
        setMaxLife(50);
        setAllowedNumberOfBullets(4);
        setAllowedMoveInterval(250);
        setAllowedFireInterval(500);
        setBulletTracker(new BulletTracker(this, 256));
        isBuilding = false;
        isDismantling = false;
        buildTimer = new Timer();
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
        //cannot move while building
        if (isBuilding) {
            return false;
        }
        //Can enter forest
        //Entering hilly terrain takes 50% longer, water takes 100% longer
        FieldHolder nextField = getParent().getNeighbor(direction);
        Terrain nextTerrain = nextField.getTerrain();
        Improvement i = null;
        if (nextField.isImproved()) {
            i = nextField.getImprovement();
            millis = i.mutateTime(millis, getAllowedMoveInterval());
        }
        long lastMoveTime = getLastMoveTime();
        if (nextTerrain == Terrain.Hilly) {
            millis = millis - (getAllowedMoveInterval() / 2);
        } else if (nextTerrain == Terrain.Water) {
            if (i != null) {
                if (!i.isDock()) {
                    millis = millis - (getAllowedMoveInterval());
                }
            } else {
                millis = millis - (getAllowedMoveInterval());
            }
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

    @Override
    public int move(long millis, Direction direction) {
        int result = super.move(millis, direction);
        isDismantling = false;
        return result;
    }

    /**
     * {@inheritDoc} 250ms base turn speed. Can only turn 90 degrees.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will turn
     * @return {@inheritDoc}
     */
    @Override
    public boolean canTurn(long millis, Direction direction) {
        //cannot turn while building
        if (isBuilding) {
            return false;
        }
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
        isDismantling = false;
    }

    @Override
    public FieldEntity copy() {
        return new Builder(getId(), getPlayer(), getIp(), getAccountID());
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

    /**
     * Checks whether the builder can build the selected improvement.
     * @param mapper An enum mapper to the improvement that will be built
     * @return Whether the improvement can be built or not
     */
    public boolean canBuild(ImprovementMapper mapper) {
        //If already building
        if (isBuilding) {
            return false;
        }
        FieldHolder parent = getParent();
        //get holder behind builder
        FieldHolder behind = parent.getNeighbor(Direction.opposite(getDirection()));
        //If entity or improvement already there
        if (behind.isPresent() || behind.isImproved()) {
            return false;
        }
        //If creating dock and terrain not water
        if (mapper == ImprovementMapper.Deck && behind.getTerrain() != Terrain.Water) {
            return false;
        } else if (mapper != ImprovementMapper.Deck && behind.getTerrain() == Terrain.Water) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the builder can dismantle the improvement behind them.
     * @return Whether the improvement can be dismantled or not
     */
    public boolean canDismantle() {
        //If already building or destroying
        if (isBuilding || isDismantling) {
            return false;
        }
        //get holder behind builder & improvement
        FieldHolder behind = parent.getNeighbor(Direction.opposite(getDirection()));
        //Is there an improvement
        return behind.isImproved();
    }

    /**
     * Updates the users credits and starts the dismantle timer.
     */
    public void startDismantle() {
        //get holder behind builder
        FieldHolder behind = getParent().getNeighbor(Direction.opposite(getDirection()));
        Improvement i = behind.getImprovement();
        if (!i.sellImprovement(getAccountID())) {
            return;
        }
        isDismantling = true;
        //set timer for dismantling
        byte time = 2;
        if (behind.getTerrain() == Terrain.Normal || behind.getTerrain() == Terrain.Water) {
            time = 1;
        }
        buildTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //If still dismantling, clear improvement
                if (isDismantling) {
                    behind.clearImprovement();
                    isDismantling = false;
                }
            }
        }, TimeUnit.SECONDS.toMillis(time));
    }

    /**
     * Updates the users credits, creates the new improvement, starts the build timer.
     * @param mapper An enum mapper to the improvement that will be built
     */
    public void startBuilding(ImprovementMapper mapper) {
        FieldHolder parent = getParent();
        //get holder behind builder
        FieldHolder behind = parent.getNeighbor(Direction.opposite(getDirection()));
        //Create improvement & spend credits
        Improvement improvement = mapImprovement(mapper);
        if (!improvement.buyImprovement(getAccountID())) {
            return;
        }
        isBuilding = true;
        //set timer for building
        byte time = 2;
        if (behind.getTerrain() == Terrain.Normal || behind.getTerrain() == Terrain.Water) {
            time = 1;
        }
        buildTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Place the improvement
                behind.setImprovement(improvement);
                isBuilding = false;
            }
        }, TimeUnit.SECONDS.toMillis(time));
        //TODO: ADD EVENT (Low priority, we're never finishing event system)
    }

    private Improvement mapImprovement(ImprovementMapper mapper) {
        if (mapper == ImprovementMapper.Road) {
            return new Road();
        } else if (mapper == ImprovementMapper.Deck) {
            return new Deck();
        } else {
            return new Wall(-1, 1000);
        }
    }
}
