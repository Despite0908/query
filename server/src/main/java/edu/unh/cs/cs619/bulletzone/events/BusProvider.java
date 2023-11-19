package edu.unh.cs.cs619.bulletzone.events;

import com.google.common.eventbus.EventBus;

public class BusProvider {
    public EventBus eventBus;
    private static BusProvider single_instance = null;

    private BusProvider() {
        eventBus = new EventBus();
    }

    public static synchronized BusProvider BusProvider()
    {
        if (single_instance == null)
        {
            single_instance = new BusProvider();
        }
        return single_instance;
    }

}
