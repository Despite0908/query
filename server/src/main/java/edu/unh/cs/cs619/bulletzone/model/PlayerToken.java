package edu.unh.cs.cs619.bulletzone.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class PlayerToken extends FieldEntity{

    private final long id;

    private final String ip;

    private long lastMoveTime;
    private int allowedMoveInterval;

    private long lastFireTime;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private int life;
    private BulletTracker bulletTracker;

    private Direction direction;

    /**
     * Constructor. Handles common data and functionality between tokens.
     * @param id The ID of the token
     * @param direction The initial direction of the token
     * @param ip IP of the player
     */
    public PlayerToken(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        numberOfBullets = 0;
        lastFireTime = 0;
        lastMoveTime = 0;
    }

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
    public boolean move(long millis, Direction direction) {
        //Set new timestamp
        setLastMoveTime(millis + getAllowedMoveInterval());

        //Move the tank from parent to nextField
        FieldHolder parent = getParent();

        FieldHolder nextField = parent.getNeighbor(direction);
        checkNotNull(parent.getNeighbor(direction), "Neighbor is not available");

        boolean isCompleted;
        if (!nextField.isPresent()) {
            // If the next field is empty move the user
            parent.clearField();
            nextField.setFieldEntity(this);
            setParent(nextField);
            return true;
        }
        return false;
    }

    /**
     * Constraint checking on the token's fire operation.
     * @param millis Timestamp in milliseconds
     * @return Whether the token can fire or not
     */
    public boolean canFire(long millis) {
        if(getNumberOfBullets() >= getAllowedNumberOfBullets())
            return false;
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

    @JsonIgnore
    public long getId() {
        return id;
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
}
