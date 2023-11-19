package edu.unh.cs.cs619.bulletzone.events;

import com.google.common.eventbus.EventBus;

// @Component
public class EventDispatcher {

    // @Autowired
    public EventDispatcher(EventBus eventBus) {
        eventBus.post(new CustomEvent("test event"));
    }
}