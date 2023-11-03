package edu.unh.cs.cs619.bulletzone.model;

public class Tank extends PlayerToken {

    private static final String TAG = "Tank";

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
        setAllowedFireInterval(500);
        setAllowedMoveInterval(500);
        setBulletTracker(new BulletTracker(this, 2));
    }

    /**
     * {@inheritDoc}
     * @param millis Timestamp in milliseconds
     * @param direction Direction in which the token will be moved
     * @return {@inheritDoc}
     */
    @Override
    public boolean canMove(long millis, Direction direction) {
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

    @Override
    public void hit(int damage) {
        int life = getLife() - damage;
        setLife(life);
        System.out.println("Tank life: " + life + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

        if (life <= 0) {
//			Log.d(TAG, "Tank event");
            //eventBus.post(Tank.this);
            //eventBus.post(new Object());
        }
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


}
