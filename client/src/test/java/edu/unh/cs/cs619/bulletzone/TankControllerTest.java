package edu.unh.cs.cs619.bulletzone;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

/**
 * Testing for the TankController class. Tests all of the methods of the class that call upon the
 * RestClient.
 * @author Nicolas Karpf
 */
@RunWith(MockitoJUnitRunner.class)
public class TankControllerTest {
    @Mock
    ClientActivity activity;
    @Mock
    BulletZoneRestClient restClient;

    @Test
    public void turnLeft_onTankId_callsRestClientTurn() {
        TankController controller = new TankController(activity);
        controller.joinAsync();
        controller.injectRestClient(restClient);
        controller.turnLeft();
        verify(restClient).turn(controller.getTankId(), (byte) 6);
        controller.turnLeft();
        verify(restClient).turn(controller.getTankId(), (byte) 4);
        controller.turnLeft();
        verify(restClient).turn(controller.getTankId(), (byte) 2);
        controller.turnLeft();
        verify(restClient).turn(controller.getTankId(), (byte) 0);
    }

    @Test
    public void turnRight_onTankId_callsRestClientTurn() {
        TankController controller = new TankController(activity);
        controller.joinAsync();
        controller.injectRestClient(restClient);
        controller.turnRight();
        verify(restClient).turn(controller.getTankId(), (byte) 2);
        controller.turnRight();
        verify(restClient).turn(controller.getTankId(), (byte) 4);
        controller.turnRight();
        verify(restClient).turn(controller.getTankId(), (byte) 6);
        controller.turnRight();
        verify(restClient).turn(controller.getTankId(), (byte) 0);
    }

    @Test
    public void moveTank_withUpButton_callsRestClientMove() {
        TankController controller = new TankController(activity);
        controller.joinAsync();
        controller.injectRestClient(restClient);
        controller.moveTank(R.id.buttonUp);
        verify(restClient).move(controller.getTankId(), (byte) 0);
        controller.turnRight();
        controller.moveTank(R.id.buttonUp);
        verify(restClient).move(controller.getTankId(), (byte) 2);
        controller.turnRight();
        controller.moveTank(R.id.buttonUp);
        verify(restClient).move(controller.getTankId(), (byte) 4);
        controller.turnRight();
        controller.moveTank(R.id.buttonUp);
        verify(restClient).move(controller.getTankId(), (byte) 6);
    }

    @Test
    public void moveTank_withDownButton_callsRestClientMove() {
        TankController controller = new TankController(activity);
        controller.joinAsync();
        controller.injectRestClient(restClient);
        controller.moveTank(R.id.buttonDown);
        verify(restClient).move(controller.getTankId(), (byte) 4);
        controller.turnRight();
        controller.moveTank(R.id.buttonDown);
        verify(restClient).move(controller.getTankId(), (byte) 6);
        controller.turnRight();
        controller.moveTank(R.id.buttonDown);
        verify(restClient).move(controller.getTankId(), (byte) 0);
        controller.turnRight();
        controller.moveTank(R.id.buttonDown);
        verify(restClient).move(controller.getTankId(), (byte) 2);
    }

    @Test
    public void onBulletFire_onTankId_callsRestClientFire() {
        TankController controller = new TankController(activity);
        controller.joinAsync();
        controller.injectRestClient(restClient);
        controller.onBulletFire();
        verify(restClient).fire(controller.getTankId(), 1);
    }

    @Test
    public void joinAsync_inController_callsRestClientJoin() {
        TankController controller = new TankController(activity);
        controller.injectRestClient(restClient);
        controller.joinAsync();
        verify(restClient).join();
    }

    @Test
    public void leaveAsync_inController_callsRestClientLeave() {
        TankController controller = new TankController(activity);
        controller.injectRestClient(restClient);
        controller.joinAsync();
        controller.leaveAsync();
        verify(restClient).leave(controller.getTankId());
    }

    @Test
    public void afterInject_inController_callsRestClientSetRestErrorHandler() {
        TankController controller = new TankController(activity);
        controller.injectRestClient(restClient);
        controller.joinAsync();
        controller.afterInject();
        verify(restClient).setRestErrorHandler(controller.bzRestErrorhandler);
    }
}