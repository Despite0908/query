package edu.unh.cs.cs619.bulletzone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
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
import org.springframework.web.client.RestClientException;
import org.w3c.dom.Text;

import java.util.Locale;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BalanceUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.GridPollerTask;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;
import edu.unh.cs.cs619.bulletzone.ui.GameUser;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.DoubleWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;

@EActivity(R.layout.activity_client)
public class ClientActivity extends Activity {

    private static final String TAG = "ClientActivity";

    @Bean
    protected GridAdapter mGridAdapter;

    GameUser user;
    int cachedID;

    @ViewById
    protected GridView gridView;

    @Bean
    BusProvider busProvider;

    @NonConfigurationInstance
    @Bean
    GridPollerTask gridPollTask;
    @RestService
    BulletZoneRestClient restClient;
    @Bean
    BZRestErrorhandler bzRestErrorhandler;

//    @RestService
//    BulletZoneRestClient restClient;

//    @Bean
//    BZRestErrorhandler bzRestErrorhandler;

    @Bean
    TankController tankControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cachedID = -1;
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

    /**
     * When this activity resumes, check to see if account has changed. If it has,
     * change account information and rejoin the game.
     */
    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateInventoryRunnable);
        if (cachedID != user.getId()) {
            //login has changed, change UI
            cachedID = user.getId();
            gridPollTask.setId(cachedID);
            TextView usernameView = findViewById(R.id.username);
            usernameView.setText(user.getUsername());
            //leave and re-join game
            tankControl.reJoinAsync(cachedID);
            SystemClock.sleep(1000);
            mGridAdapter.setPlayerTankId(tankControl.getTankId());
            mGridAdapter.setPlayerBuilderId(tankControl.getBuilderId());
            mGridAdapter.setPlayerSoldierId(-1);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        busProvider.getEventBus().unregister(gridEventHandler);
        busProvider.getEventBus().unregister(balanceEventHandler);
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

    private Object balanceEventHandler = new Object()
    {
        @Subscribe
        public void onUpdateBalance(BalanceUpdateEvent event) {
            updateBalance(event.dw);
        }
    };


    @AfterViews
    protected void afterViewInjection() {
        tankControl.joinAsync(user.getId());
        gridPollTask.doPoll();
        SystemClock.sleep(500);
        gridView.setAdapter(mGridAdapter);
        mGridAdapter.setPlayerTankId(tankControl.getTankId());
        mGridAdapter.setPlayerBuilderId(tankControl.getBuilderId());
        mGridAdapter.setTankController(tankControl);
    }

    @AfterInject
    void afterInject() {
        tankControl.afterInject();
        busProvider.getEventBus().register(gridEventHandler);
        busProvider.getEventBus().register(balanceEventHandler);

        restClient.setRestErrorHandler(bzRestErrorhandler);

        //Cam: work in progress
        user = GameUser.getInstance();
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

        //getPlayerInfo();
    }

    public void updateBalance(DoubleWrapper dw) {
        TextView creditsView = findViewById(R.id.balance);
        creditsView.setText(String.format(Locale.ENGLISH, "Credits: %d", (long) dw.getResult()));
    }

    public void getPlayerInfo() {

        InventoryWrapper in = restClient.getInventory(user.getId());

        int[] inv = in.getResult();


        String text = "Credits: " + inv[0] + "\n";

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

    @Click(R.id.buttonSwitch)
    protected void onButtonSwitch() {
        tankControl.setBuilderFocus(!tankControl.isBuilderFocus());
        TextView v = findViewById(R.id.buttonSwitch);
        if (!tankControl.isBuilderFocus()) {
            v.setText(getResources().getString(R.string.switch_builder));
        } else {
            if (tankControl.getSoldierId() == -1) {
                v.setText(getResources().getString(R.string.switch_tank));
            } else {
                v.setText(getResources().getString(R.string.switch_soldier));
            }
        }
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

    
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateInventoryRunnable);
    }

    private final Handler handler = new Handler();
    private final Runnable updateInventoryRunnable = new Runnable() {
        @Override
        public void run() {
            int ID = user.getId();
            fetchAndDisplayInventory(ID);
            handler.postDelayed(this, 500);

        }
    };

    private void fetchAndDisplayInventory(int ID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InventoryWrapper inventoryWrapper = restClient.getInventory(ID);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (inventoryWrapper != null) {
                                int[] data = inventoryWrapper.getResult(); // Corrected typo here
                                int tankHealth = data[1];
                                int soldierHealth = data[2];
                                //int credits = inventoryWrapper.getCredits();

                                // Update your UI with these values
                                updateGameUI(tankHealth, soldierHealth);
                            } else {
                                // Handle the case where inventoryWrapper is null
                                //handleInventoryFetchError();
                            }
                        }
                    });
                } catch (RestClientException e) {
                    e.printStackTrace();
                    // Optionally handle the error on UI thread

                }
            }
        }).start();
    }


    protected void updateGameUI(final int tankHealth, final int soldierHealth) {
        runOnUiThread(new Runnable() {
            @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
            @Override
            public void run() {
                // Update Soldier Health
                ProgressBar soldierHealthBar = findViewById(R.id.soldierHealthBar);
                TextView soldierHealthTextView = findViewById(R.id.soldierHealthValue);

                soldierHealthBar.setMax(25);
                soldierHealthTextView.setText(soldierHealth + "|" + "25");
                updateHealthBarColor(soldierHealth, soldierHealthBar, 25);

                // Update Tank Health
                ProgressBar tankHealthBar = findViewById(R.id.tankHealthBar);
                TextView tankHealthTextView = findViewById(R.id.tankHealthValue);

                tankHealthBar.setMax(100);
                tankHealthTextView.setText(tankHealth + "|" + "100");
                updateHealthBarColor(tankHealth, tankHealthBar, 100);

            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateHealthBarColor(int health, ProgressBar healthBar, int maxHealth) {
        int thresholdOne = (int) (maxHealth * 0.66);
        int thresholdTwo = (int) (maxHealth * 0.33);

        if (health > thresholdOne) {
            healthBar.setProgressDrawable(getResources().getDrawable(R.drawable.health_bar));
        } else if (health > thresholdTwo) {
            healthBar.setProgressDrawable(getResources().getDrawable(R.drawable.health_baryellow));
        } else {
            healthBar.setProgressDrawable(getResources().getDrawable(R.drawable.health_barred));
        }
        healthBar.setProgress(health);
    }


    @Click(R.id.buttonLogin)
    void login() {
        Intent intent = new Intent(this, AuthenticateActivity_.class);
        startActivity(intent);
    }
}
