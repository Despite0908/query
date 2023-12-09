package edu.unh.cs.cs619.bulletzone.rest;

import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.rest.spring.annotations.RestService;
import org.androidannotations.rest.spring.api.RestClientHeaders;

import edu.unh.cs.cs619.bulletzone.ClientActivity;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.util.DoubleWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

/**
 * Created by simon on 10/3/14.
 */
@EBean
public class GridPollerTask {
    private static final String TAG = "PollServer";

    // Injected object
    @Bean
    BusProvider busProvider;
    long id = -1;

    Object monitor = new Object();

    @RestService
    BulletZoneRestClient restClient;

    @Background(id = "grid_poller_task")
    // TODO: disable trace
    // @Trace(tag="CustomTag", level=Log.WARN)
    public void doPoll() {
        while (true) {

            onGridUpdate(restClient.grid());

            synchronized (monitor) {
                if (id != -1) {
                    onCreditUpdate(restClient.balance(id));
                }
            }

            // poll server every 100ms
            SystemClock.sleep(100);
        }
    }

    @UiThread
    public void onGridUpdate(GridWrapper gw) {
        busProvider.getEventBus().post(new GridUpdateEvent(gw));
    }

    @UiThread
    public void onCreditUpdate(DoubleWrapper dw) {
        busProvider.getEventBus().post(new BalanceUpdateEvent(dw));
    }

    public void setId(long id) {
        synchronized (monitor) {
            this.id = id;
        }
    }
}
