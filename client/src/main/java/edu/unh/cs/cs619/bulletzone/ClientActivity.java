package edu.unh.cs.cs619.bulletzone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.androidannotations.api.BackgroundExecutor;
import org.json.JSONException;
import org.w3c.dom.Text;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    protected GridAdapter mGridAdapter;

    @ViewById
    protected GridView gridView;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;

//    @RestService
//    BulletZoneRestClient restClient;

//    @Bean
//    BZRestErrorhandler bzRestErrorhandler;

    @Bean
    TankController tankControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event != null) {
                    float xAccel = event.values[0];
                    float yAccel = event.values[1];
                    //float zAccel = event.values[2];

                    if (xAccel > 2 || xAccel < -2 || yAccel > 12 || yAccel < -12) {
                        tankControl.onBulletFire();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.getEventBus().unregister(gridEventHandler);
    }

    /**
     * Otto has a limitation (as per design) that it will only find
     * methods on the immediate class type. As a result, if at runtime this instance
     * actually points to a subclass implementation, the methods registered in this class will
     * not be found. This immediately becomes a problem when using the AndroidAnnotations
     * framework as it always produces a subclass of annotated classes.
     *
     * To get around the class hierarchy limitation, one can use a separate anonymous class to
     * handle the events.
     */
    private Object gridEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateGrid(GridUpdateEvent event) {
            updateGrid(event.gw);
        }
    };


    @AfterViews
    protected void afterViewInjection() {
        tankControl.joinAsync();
        gridPollTask.doPoll();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        mGridAdapter.setPlayerTankId(tankControl.getTankId());
        mGridAdapter.setTankController(tankControl);
    }

    @AfterInject
    void afterInject() {
        tankControl.afterInject();
        busProvider.getEventBus().register(gridEventHandler);
//        tankControl.afterInject(busProvider, gridEventHandler);
    }

//    @Background
//    void joinAsync() {
//        try {
//            tankId = restClient.join().getResult();
//            gridPollTask.doPoll();
//        } catch (Exception e) {
//        }
//    }

    public void updateGrid(GridWrapper gw) {
        mGridAdapter.updateList(gw.getGrid());
    }

    @Click({R.id.buttonUp, R.id.buttonDown})
    @Background
    protected void onButtonMove(View view) {
        final int viewId = view.getId();
        long moveResult = tankControl.moveTank(viewId);
        if (moveResult == 2) {
            mGridAdapter.setPlayerSoldierId(-1);
        }
//        this.moveAsync(tankId, tankControl.moveTank(viewId));
    }

    @Click({R.id.buttonLeft})
    @Background
    protected void onButtonTurnLeft() {
        tankControl.turnLeft();
//        this.turnAsync(tankId, tankControl.turnLeft());
    }

    @Click({R.id.buttonRight})
    @Background
    protected void onButtonTurnRight(View view) {
        tankControl.turnRight();
//        this.turnAsync(tankId, tankControl.turnRight());
    }

//    @Background
//    void moveAsync(long tankId, byte direction) {
//        restClient.move(tankId, direction);
//    }

//    @Background
//    void turnAsync(long tankId, byte direction) {
//        restClient.turn(tankId, direction);
//    }

    @Click(R.id.buttonFire)
    @Background
    protected void onButtonFire() {
        tankControl.onBulletFire();
    }

    @Click(R.id.buttonEject)
    @Background
    protected void onButtonEject() {
        tankControl.eject(tankControl.getTankId());
        mGridAdapter.setPlayerSoldierId(tankControl.getSoldierId());
    }

    @Click(R.id.buttonLeave)
    @Background
    void leaveGame() {
        onLeavePressed();
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        onLeavePressed();
    }

    public void onLeavePressed() {
        Thread thread = new Thread(){
            public void run(){
                runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(ClientActivity.this)
                                .setTitle("Exit App")
                                .setMessage("Do you really want to exit?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        System.out.println("leaveGame() called, tank ID: "+tankControl.getTankId());
                                        BackgroundExecutor.cancelAll("grid_poller_task", true);
                                        tankControl.leaveAsync();
                                        //ClientActivity.super.onBackPressed();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }
        };
        thread.start();
    }


    protected void updateTankHealth(final int health) {
        runOnUiThread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {

                ProgressBar tankHealth = findViewById(R.id.tankHealthBar);
                TextView tankTextView = findViewById(R.id.tankHealthValue);

                tankHealth.setMax(100);
                Log.d("ProgressBarDebug", "Current progress: " + tankHealth.getProgress());


                tankTextView.setText(health + "|" + "100");

                if (health > 66) {
                    // Green color for health > 66%
                    tankHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_bar));
                } else if (health > 33) {
                    // Yellow color for health between 33% and 66%
                    tankHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_baryellow));
                } else {
                    // Red color for health <= 33%
                    tankHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_barred));
                }
                tankHealth.setProgress(health);

            }
        });

    }

    protected void updateSoldierHealth(final int health) {
        runOnUiThread(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {

                ProgressBar soldierHealth = findViewById(R.id.soldierHealthBar);
                TextView soldierTextView = findViewById(R.id.soldierHealthValue);

                soldierHealth.setMax(25);
                Log.d("ProgressBarDebug", "Current progress: " + soldierHealth.getProgress());


                soldierTextView.setText(health + "|" + "25");

                if (health > 16) {
                    // Green color for health > 66%
                    soldierHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_bar));
                } else if (health > 8) {
                    // Yellow color for health between 33% and 66%
                    soldierHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_baryellow));
                } else {
                    // Red color for health <= 33%
                    soldierHealth.setProgressDrawable(getResources().getDrawable(R.drawable.health_barred));
                }
                soldierHealth.setProgress(health);

            }
        });

    }




    @Click(R.id.buttonLogin)
    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivity(intent);
    }
}
