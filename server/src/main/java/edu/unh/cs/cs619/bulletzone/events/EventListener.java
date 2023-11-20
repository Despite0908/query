package edu.unh.cs.cs619.bulletzone.events;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class EventListener {
    private int eventsHandled;

    //@Autowired
    public EventListener(EventBus eventBus) {
        eventBus.register(this); // register this instance with the event bus so it receives any events
    }

    @Subscribe
    public void someCustomEvent(CustomEvent customEvent) {
        eventsHandled++;
        //System.out.println("RECEIVED EVENT!!!!\n\n\n\n");
    }

    public int getEventHandled(){
        return eventsHandled;
    }
}
