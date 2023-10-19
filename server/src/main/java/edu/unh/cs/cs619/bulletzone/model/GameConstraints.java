package edu.unh.cs.cs619.bulletzone.model;

public class GameConstraints {

    public static boolean checkMoveInterval(Tank tank, long millis) {
        return millis >= tank.getLastMoveTime();
    }

    public static boolean checkFireInterval(Tank tank, long millis) {
        return millis >= tank.getLastFireTime();
    }

    public static boolean checkBulletsFull(Tank tank) {
        return tank.getNumberOfBullets() >= tank.getAllowedNumberOfBullets();
    }

    public static boolean checkTurnConstraints(Tank tank, Direction direction) {

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

    public static boolean checkMoveConstraints(Tank tank, Direction direction) {
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
