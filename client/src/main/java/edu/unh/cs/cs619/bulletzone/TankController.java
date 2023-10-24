package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.util.Log;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

public class TankController {
    private static TankController instance;
    private byte direction;
    private Activity clientActivity;
    private BulletZoneRestClient restClient;
    private TankController(Activity activity, BulletZoneRestClient bzrest) {
        direction = 0;
        clientActivity = activity;
        restClient = bzrest;
    }

    public static TankController getInstance(Activity activity, BulletZoneRestClient bzrest) {
        if (instance == null) {
            instance = new TankController(activity, bzrest);
        }
        return instance;
    }

    public void turnLeft(long tankId) {
        if (direction == 0) {
            direction = 6;
        } else {
            direction -= 2;
        }
        restClient.turn(tankId, direction);
    }

    public void turnRight(long tankId) {
        if (direction == 6) {
            direction = 0;
        } else {
            direction += 2;
        }
        restClient.turn(tankId, direction);
    }

    public byte getDirection() {
        return direction;
    }

    public void moveTank(int viewId, long tankId) {
        byte placeHolderDirection = getDirection();
        switch (viewId) {
            case R.id.buttonUp:
                break;
            case R.id.buttonDown:
                if (placeHolderDirection >= 4) {
                    placeHolderDirection -= 4;
                } else {
                    placeHolderDirection += 4;
                }
                break;
            default:
                Log.e("ClientActivity", "Unknown movement button id: " + viewId);
                break;
        }
        restClient.move(tankId, placeHolderDirection);
    }
}
