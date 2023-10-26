package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Tank extends FieldEntity {

    private static final String TAG = "Tank";

    private final long id;

    private final String ip;

    private long lastMoveTime;
    private int allowedMoveInterval;

    private long lastFireTime;
    private int allowedFireInterval;

    private int numberOfBullets;
    private int allowedNumberOfBullets;

    private int life;

    private TankConstraints tankConstraints;
    private BulletTracker bulletTracker;

    private Direction direction;

    public Tank(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        numberOfBullets = 0;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;
        allowedFireInterval = 500;
        lastMoveTime = 0;
        allowedMoveInterval = 500;
        tankConstraints = new TankConstraints(this);
        bulletTracker = new BulletTracker(this);
    }

    /**
     * Getter for the tank's bullet tracker.
     * @return The tank's BulletTracker object.
     */
    public BulletTracker getBulletTracker() {
        return bulletTracker;
    }

    /**
     * Helper function, calls relevant move functionality from GameConstraints.
     * @param millis Current timestamp in milliseconds
     * @param direction Direction tank is to be moved
     * @return Boolean value as to whether the move passed the constraints
     */
    public boolean moveConstraints(long millis, Direction direction) {
        if (!tankConstraints.checkMoveInterval(millis)) {
            return false;
        }
        return tankConstraints.checkMoveConstraints(direction);
    }

    /**
     * Helper function, calls relevant turn functionality from GameConstraints.
     * @param millis Current timestamp in milliseconds
     * @param direction Direction tank is to be moved
     * @return Boolean value as to whether the turn passed constraints
     */
    public boolean turnConstraints(long millis, Direction direction) {
        if (!tankConstraints.checkMoveInterval(millis)) {
            return false;
        }
        return tankConstraints.checkTurnConstraints(direction);
    }

    /**
     * Helper function, calls relevant fire functionality from GameConstraints.
     * @param millis Current timestamp in milliseconds
     * @return Boolean value as to whether the fire passed constraints
     */
    public boolean fireConstraints(long millis) {
        if(tankConstraints.checkBulletsFull())
            return false;
        return tankConstraints.checkFireInterval(millis);
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    @Override
    public void hit(int damage) {
        life = life - damage;
        System.out.println("Tank life: " + id + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

        if (life <= 0) {
//			Log.d(TAG, "Tank event");
            //eventBus.post(Tank.this);
            //eventBus.post(new Object());
        }
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

    public long getAllowedFireInterval() {
        return allowedFireInterval;
    }

    public void setAllowedFireInterval(int allowedFireInterval) {
        this.allowedFireInterval = allowedFireInterval;
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

    @Override
    public int getIntValue() {
        return (int) (10000000 + 10000 * id + 10 * life + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
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
