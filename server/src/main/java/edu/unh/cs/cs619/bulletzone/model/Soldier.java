package edu.unh.cs.cs619.bulletzone.model;

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
        setAllowedFireInterval(250);
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
        return "T";
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
}
