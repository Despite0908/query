package edu.unh.cs.cs619.bulletzone.model.entities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.eventbus.EventBus;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.events.CustomEvent;
import edu.unh.cs.cs619.bulletzone.model.BulletTracker;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;

public abstract class PlayerToken extends FieldEntity{

    private final String ip;

    private long lastMoveTime;
    private long allowedMoveInterval;

    private long lastFireTime;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private int life;
    private BulletTracker bulletTracker;

    private Direction direction;

    PlayerToken pair;

    //EventBus eventBus = new BusProvider().getEventBus();


    //this is what was working
    //EventBus eventBus;

    EventBus eventBus2;




    /**
     * Constructor. Handles common data and functionality between tokens.
     * @param id The ID of the token
     * @param direction The initial direction of the token
     * @param ip IP of the player
     */
    public PlayerToken(long id, Direction direction, String ip) {
        super(id);
        this.direction = direction;
        this.ip = ip;
        numberOfBullets = 0;
        lastFireTime = 0;
        lastMoveTime = 0;
        pair = null;
        eventBus2 = BusProvider.BusProvider().eventBus;
    }

//    public void setEventBus(EventBus theBus) {
//        eventBus = theBus;
//    }

//    public EventBus getEventBus() {
//        return eventBus;
//    }
    /**
     * Constraint checking for the token's turn operation.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will turn
     * @return Whether the token can turn or not
     */
    public abstract boolean canTurn(long millis, Direction direction);

    /**
     * Turns the token and updates relevant token information.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the tank will be turned
     */
    public abstract void turn(long millis, Direction direction);

    /**
     * Constraint checking for the token's move operation.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return Whether the token can move or not
     */
    public abstract boolean canMove(long millis, Direction direction);

    /**
     * Moves the token and updates relevant token information.
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the tank will be moved
     * @return Whether the move was successful or not
     */
    public int move(long millis, Direction direction) {
        //Set new timestamp
        setLastMoveTime(millis + getAllowedMoveInterval());

        //Move the tank from parent to nextField
        FieldHolder parent = getParent();

        FieldHolder nextField = parent.getNeighbor(direction);
        checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

        //Check for walls
        if (nextField.isImproved() && nextField.getImprovement().isSolid()) {
            return 0;
        }

        //Check for Items
        if (nextField.isPresent() && nextField.getEntity().getIsItem()) {
            Item grabbedItem = (Item) nextField.getEntity();
            parent.clearField();
            nextField.setFieldEntity(this);
            setParent(nextField);
            CustomEvent customEvent = new CustomEvent("Custom Event");
            if (grabbedItem.getItemType() == 2003) {
                numBulletsAfterReactor();
                fireRateAfterReactor();
            }
            // this was working
            // eventBus.post(customEvent);
            eventBus2.post(customEvent);
            return 1;
        }


        //Check for entities
        boolean isCompleted;
        if (!nextField.isPresent()) {
            // If the next field is empty move the user
            parent.clearField();
            nextField.setFieldEntity(this);
            setParent(nextField);
            return 1;
        }
        return nextField.getEntity().movedIntoBy(this);
    }

    /**
     * Constraint checking on the token's fire operation.
     * @param millis Timestamp in milliseconds
     * @return Whether the token can fire or not
     */
    public boolean canFire(long millis) {
        System.out.printf("Allowed %d bullets, there were %d\n", getAllowedNumberOfBullets(), getNumberOfBullets());
        if(getNumberOfBullets() >= getAllowedNumberOfBullets()) {
            return false;
        }
        return millis >= getLastFireTime();
    }

    public BulletTracker getBulletTracker() {
        return bulletTracker;
    }

    public void setBulletTracker(BulletTracker bulletTracker) {
        this.bulletTracker = bulletTracker;
    }


    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getAllowedMoveInterval() {
        return allowedMoveInterval;
    }

    public void setAllowedMoveInterval(int allowedMoveInterval) {
        this.allowedMoveInterval = allowedMoveInterval;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }

    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets;
    }

    public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
        this.allowedNumberOfBullets = allowedNumberOfBullets;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }


    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getIp(){
        return ip;
    }

    public PlayerToken getPair() {
        return pair;
    }

    public void setPair(PlayerToken pair) {
        this.pair = pair;
    }

    /**
     * Doubles number of bullets to fired
     * @return num of bullets that can be fired
     */
    public void numBulletsAfterReactor() {
        setAllowedNumberOfBullets(allowedNumberOfBullets * 2);
        //setNumberOfBullets(numberOfBullets * 2);
    }

    /**
     * doubles allowed firing time
     * @return interval of firing time
     */
    public void fireRateAfterReactor() {
        setLastFireTime(lastFireTime / 2);
    }

//    public long movementSpeedAfterReactor() {
//        double delay = allowedMoveInterval * .25;
//        setAllowedMoveInterval(delay);
//    }

}
