package edu.unh.cs.cs619.bulletzone.model.ServerEvents;


import java.time.Clock;
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

    private Clock c;
    private volatile static EventHistory _instance;

    private EventHistory(Clock c) {
        this.events = new ArrayList<>();
        this.c = c;
    }

    public Clock getClock() {
        return c;
    }

    public static EventHistory get_instance() {
        if (_instance == null) {
            return null;
        }
        return _instance;
    }

    public static EventHistory start(Clock c) {
        synchronized (EventHistory.class) {
            _instance = new EventHistory(c);
        }
        return _instance;
    }

    /**
     * Add an event to the history that will be destroyed after 3 minutes
     * @param gridEvent Event to be added
     */
    public void addEvent(GridEvent gridEvent) {
        events.add(gridEvent);
    }

    /**
     * Get a list of events that occurred after a certain point in time.
     * @param millis Timestamp in milliseconds
     * @return Events that occurred after millis
     */
    public List<GridEvent> getEventsAfter(long millis) {
        List<GridEvent> returnList = new ArrayList<>();
        for (int i = events.size() - 1; i >=0 ; i--) {
            if (events.get(i).getMillis() <= millis - TimeUnit.MINUTES.toMillis(2)) {
                events.remove(events.get(i));
            }
        }

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getMillis() > millis) {
                returnList.add(events.get(i));
            }
        }
        return returnList;
    }

    public void clear() {
        events.clear();
    }
}
