package edu.unh.cs.cs619.bulletzone.model.ServerEvents;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A class to store the history of board events in the past 3 minutes.
 * @author Anthony Papetti
 */
public class EventHistory {

    private List<GridEvent> events;
    private Timer t;
    private volatile static EventHistory _instance;

    private EventHistory() {
        this.events = new ArrayList<>();
        this.t = new Timer();
    }

    public static EventHistory get_instance() {
        if (_instance == null) {
            synchronized (EventHistory.class) {
                if (_instance == null) {
                    _instance = new EventHistory();
                }
            }
        }
        return _instance;
    }

    /**
     * Add an event to the history that will be destroyed after 3 minutes
     * @param gridEvent Event to be added
     */
    public void addEvent(GridEvent gridEvent) {
        events.add(gridEvent);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                events.remove(gridEvent);
            }
        }, TimeUnit.MINUTES.toMillis(3));
    }

    /**
     * Get a list of events that occurred after a certain point in time.
     * @param millis Timestamp in milliseconds
     * @return Events that occurred after millis
     */
    public List<GridEvent> getEventsAfter(long millis) {
        List<GridEvent> returnList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getMillis() > millis) {
                returnList.add(events.get(i));
            }
        }
        return returnList;
    }
}
