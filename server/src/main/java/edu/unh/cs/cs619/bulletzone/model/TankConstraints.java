package edu.unh.cs.cs619.bulletzone.model;

/**
 * A class that checks constraints that are applied to operations such as firing, moving, and
 * turning. Constraints are applied to the Tank object passed into the constructor and checked
 * against the parameters of the check or properties of the tank
 * @author Anthony Papetti
 */

public class TankConstraints {

    Tank tank;

    /**
     * Constructor to set the tank.
     * @param tank Tank that the constraints will be applied to.
     */
    public TankConstraints(Tank tank) {
        this.tank = tank;
    }

    /**
     * Checks whether tank is allowed to move based on it's time interval between move/turn
     * actions.
     * @param millis A timestamp in ms.
     * @return Whether the tank has made it out of the interval where it is blocked from moving.
     */
    public boolean checkMoveInterval(long millis) {
        return millis >= tank.getLastMoveTime();
    }

    /**
     * Checks whether tank is allowed to fire again based on it's time interval between fire
     * actions.
     * @param millis A timestamp in ms.
     * @return Whether the tank has made it out of the interval where it is blocked from moving.
     */
    public boolean checkFireInterval(long millis) {
        return millis >= tank.getLastFireTime();
    }

    /**
     * Checks whether the tank has too many bullets on the field (2) to fire again.
     * @return Returns whether the tank has too many bullets on the field to fire again.
     */
    public boolean checkBulletsFull() {
        return tank.getNumberOfBullets() >= tank.getAllowedNumberOfBullets();
    }

    /**
     * Checks if the tank's movement from tank.direction to direction is a "90 degree" turn or not.
     * @param direction The direction the tank will turn to.
     * @return Whether the turn from tank.direction to direction is a 90 degree turn.
     */
    public boolean checkTurnConstraints(Direction direction) {

        //tank only turn 90 degrees
        switch (tank.getDirection()) {
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
     * Checks to make sure the tank is not moving sideways
     * @param direction Direction that the tank is moving in
     * @return Returns true if the tank is moving straight, returns false if the tank is moving sideways
     */
    public boolean checkMoveConstraints(Direction direction) {
        //tank cannot move sideways
        if (tank.getDirection() == Direction.Up || tank.getDirection() == Direction.Down) {
            if (!(direction == Direction.Up || direction == Direction.Down)) {
                return false;
            }
        } else {
            if (!(direction == Direction.Right || direction == Direction.Left)) {
                return false;
            }
        }
        return true;
    }
}
