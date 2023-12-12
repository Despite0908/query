package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Button;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.rest.spring.annotations.RestService;


import java.util.List;

import edu.unh.cs.cs619.bulletzone.model.SoldierState;
import edu.unh.cs.cs619.bulletzone.model.State;
import edu.unh.cs.cs619.bulletzone.model.TankState;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.PlayerWrapper;


/**
 * This class is meant to act as a controller class for the ClientActivity. This handles all
 * interaction with the rest client, including the rest error handling. This includes the movement
 * and turning of the tank as well (hence the name TankController). This also takes in the async
 * functions that were previously in ClientActivity and moves them to this class as well. All
 * functions called from this class come from a instantiated Bean TankController in ClientActivity.
 * @author Nicolas Karpf
 */
@EBean
public class TankController {
    private byte direction;
    private byte soldierDirection;
    private byte builderDirection;

    /**
     * remote tank identifier
     */
    private long tankId;
    /**
     * Remote Soldier identifier
     */
    private long soldierId;

    private long builderId;

    private boolean builderFocus;
    /**
     * The ClientActivity. Used just as a Context parameter for the constructor due to EBean
     * constrictions.
     */
    private Context activity;
    /**
     * Our RestClient for our client interacting with the server
     */
    @RestService
    BulletZoneRestClient restClient;
    /**
     * Error handler for the RestClient interaction
     */
    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    //state info
    private State state;


    /**
     * Constructor for the TankController class. Because TankController is an EBean, and because
     * TankController needs to be a singleton, the constructor is protected and has a parameter of
     * type Context so that the ClientActivity can be passed in the Controller's creation in the
     * ClientActivity. Sets the base direction of the tank to 0 (upward) and the tankId to -1 until
     * it is given a real value in joinAsync.
     * @param activity Used for the purposes of EBean's requirements. The parameter represents the
     *                 ClientActivity in this case.
     */
    protected TankController(Context activity) {
        direction = 0;
        soldierDirection = 0;
        tankId = -1;
        soldierId = -1;
        builderId = -1;
        builderFocus = false;
        this.activity = activity;
        state = null;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * This method is used to turn the current direction of the tank left 90° of its currently-faced
     * direction. It then gives the direction that the given tank wants to turn to the restClient to
     * interact with the server.
     */
    public void turnLeft() {
        byte placeHolderDirection = getCurrentUnitDirection();
        if (placeHolderDirection == 0) {
            placeHolderDirection = 6;
        } else {
            placeHolderDirection -= 2;
        }
        if (!(restClient.turn(getCurrentUnitId(), placeHolderDirection).isResult())) {
            return;
        }
        setCurrentUnitDirection(placeHolderDirection);
    }
    /**
     * This method is used to turn the current direction of the tank right 90° of its currently-faced
     * direction. It then gives the direction that the given tank wants to turn to the restClient to
     * interact with the server.
     */
    public void turnRight() {
        byte placeHolderDirection = getCurrentUnitDirection();
        if (placeHolderDirection == 6) {
            placeHolderDirection = 0;
        } else {
            placeHolderDirection += 2;
        }
        if (!(restClient.turn(getCurrentUnitId(), placeHolderDirection).isResult())) {
            return;
        }
        setCurrentUnitDirection(placeHolderDirection);
    }

    /**
     * Getter for the direction the tank our user is controlling is currently facing
     * @return byte
     */
    public byte getDirection() {
        return direction;
    }

    /**
     * Getter for the direction the soldier our user is controlling is currently facing
     * @return byte
     */
    public byte getSoldierDirection() {
        return soldierDirection;
    }

    /**
     * Getter for the tankId of the tank our user is controlling.
     * @return long
     */
    public long getTankId() {
        return tankId;
    }

    public long getSoldierId() {return soldierId;}

    public void setSoldierId(long soldierId) {
        this.soldierId = soldierId;
    }

    public void setBuilderId(long builderId) {
        this.builderId = builderId;
    }

    public long getBuilderId() {
        return builderId;
    }

    public void setBuilderFocus(boolean builderFocus) {
        this.builderFocus = builderFocus;
    }

    public boolean isBuilderFocus() {
        return builderFocus;
    }

    public long getCurrentUnitId() {
        if (builderFocus) {
            return builderId;
        }
        if (soldierId != -1) {
            return soldierId;
        } else {
            return tankId;
        }
    }

    public byte getCurrentUnitDirection() {
        if (builderFocus) {
            return builderDirection;
        }
        if (soldierId != -1) {
            return soldierDirection;
        } else {
            return direction;
        }
    }

    public void setCurrentUnitDirection(byte dir) {
        if (builderFocus) {
            builderDirection = dir;
        }else if (soldierId != -1) {
            soldierDirection = dir;
        } else {
            direction = dir;
        }
    }

    //TODO: FIX DOC
    /**
     * Interacts with the RestClient to move the tank the current user is controlling either
     * forward or backward based on the button that is pressed as well as the current direction that
     * the tank is facing.
     * @param viewId Button that is pressed to move the tank (Either Up or Down)
     */
    public long moveTank(int viewId) {
        byte placeHolderDirection = getCurrentUnitDirection();
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
        long moveResult = restClient.move(getCurrentUnitId(), placeHolderDirection).getResult();
        if (moveResult == 2) {
            setSoldierId(-1);
            soldierDirection = 0;
        }
        return moveResult;
    }

    public void build(byte improvementType) {
        restClient.build(getCurrentUnitId(), improvementType);
    }

    public void dismantle() {
        restClient.dismantle(getCurrentUnitId());
    }

    /**
     * Interacts with the RestClient to fire a bullet from the tank that the user is currently
     * controlling.
     */
    @Background
    public void onBulletFire() {
        long id = getCurrentUnitId();
        boolean test;
        if (soldierId == id) {
            test = restClient.fire(id, 4).isResult();
        } else {
            test = restClient.fire(id, 1).isResult();
        }
        Log.d("fireTest", String.format("%b\n", test));
    }

    /**
     * Calls on the RestClient to have a user join the game. This sets the tankId for said user
     * that we will be using for all movement, turning, and firing that gets called in this class.
     */
    void eject(long tankId) {
        try {
            long tempId = restClient.eject(tankId).getResult();
            if (tempId != -1) {
                soldierId = tempId;
            }
        } catch (Exception e) {
            Log.d("debug", "Server Error during eject");
        }
    }

    @Background
    void joinAsync(int id) {
        try {
            PlayerWrapper p = restClient.join(id);
            tankId = p.getTankId();
            builderId = p.getBuilderId();
        } catch (Exception e) {
        }
    }

    /**
     * Has the given tankId leave the game, then joins back
     * @param accountID the current account ID
     */
    @Background
    void reJoinAsync(int accountID) {
        BooleanWrapper w = restClient.leave(tankId);
        PlayerWrapper p = restClient.join(accountID);
        tankId = p.getTankId();
        builderId = p.getBuilderId();
        direction = 0;
        soldierDirection = 0;
        soldierId = -1;
        builderFocus = false;
    }

    /**
     * Has the given tankId leave the game, and therefore the current user as well.
     */
    @Background
    void leaveAsync() {
        System.out.println("Leave called, tank ID: " + tankId);
        BackgroundExecutor.cancelAll("grid_poller_task", true);
        restClient.leave(tankId);
    }

    /**
     * Sets the error handler for the RestClient via the RestClient.
     */
    void afterInject() {
        restClient.setRestErrorHandler(bzRestErrorhandler);
    }

    void injectRestClient(BulletZoneRestClient restClient) {
        this.restClient = restClient;
    }
}
