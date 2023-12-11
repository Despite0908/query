package edu.unh.cs.cs619.bulletzone.rest;

import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;


import edu.unh.cs.cs619.bulletzone.TankController;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;

@EBean
public class HealthPollerTask {
    private static final String TAG = "HealthPoller";

    @Bean
    BusProvider busProvider;

    @RestService
    BulletZoneRestClient restClient;

    @Bean
    TankController tankControl;
    private long tankId;
    private long soldierId;
    private long builderId;

    int soldierHealth = 25;

    GridAdapter mGridAdapter;

    private volatile boolean isRunning = true;

    private boolean shouldPollSoldierHealth = false;


    public void setShouldPollSoldierHealth(boolean shouldPoll) {
        this.shouldPollSoldierHealth = shouldPoll;
    }

    public boolean shouldPollSoldierHealth() {
        return this.shouldPollSoldierHealth;
    }

    @Background(id = "health_poller_task")
    public void doPoll() {
        while (isRunning) {
            fetchAndUpdateHealth();

            // Sleep for the desired interval
            SystemClock.sleep(750); // Adjust this value as needed
        }
    }

    public void stopPolling() {
        isRunning = false;
    }

    private void fetchAndUpdateHealth() {
        try {

            int tankHealth = restClient.getTankHealth(tankId);
            int builderHealth = restClient.getBuilderHealth(builderId);
            if(shouldPollSoldierHealth) {
                if (soldierId != -1) {
                    soldierHealth = restClient.getSoldierHealth(soldierId);
                } else {
                    Log.d("Soldier", "id is " + soldierId + " and health is " + soldierHealth);

                }
            }





            onHealthUpdate(new HealthUpdateEvent(tankHealth, soldierHealth, builderHealth));
        } catch (Exception e) {
            Log.e(TAG, "Error fetching health data: " + e.getMessage());
        }
    }

    @UiThread
    public void onHealthUpdate(HealthUpdateEvent event) {
        busProvider.getEventBus().post(event);
    }

    public void setIds(long tankId, long soldierId, long builderId) {
        this.tankId = tankId;
        this.soldierId = soldierId;
        this.builderId = builderId;
    }
}

