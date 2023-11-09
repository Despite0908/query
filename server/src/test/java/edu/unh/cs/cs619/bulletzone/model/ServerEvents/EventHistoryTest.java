package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import static org.junit.Assert.*;

import static java.lang.Thread.sleep;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EventHistoryTest {

    EventHistory eventHistory;

    @Before
    public void setup() {
        eventHistory = EventHistory.get_instance();
        eventHistory.clear();
    }

    //Basic List Tests
    @Test
    public void getEventsAfter_nothingAdded_returnsEmptyList() {
        assertEquals(eventHistory.getEventsAfter(0).size(), 0);
    }

    @Test
    public void getEventsAfter_addEvent_returnsListSize1SameEvent() {
        BoardCreationEvent event = new BoardCreationEvent();
        eventHistory.addEvent(event);
        List<GridEvent> events = eventHistory.getEventsAfter(0);
        assertEquals(events.size(), 1);
        assertEquals(event, events.get(0));
    }

    @Test
    public void getEventsAfter_add10Event_returnsListSize10() {
        for (int i = 0; i < 10; i++) {
            eventHistory.addEvent(new BoardCreationEvent());
        }
        List<GridEvent> events = eventHistory.getEventsAfter(0);
        assertEquals(10, events.size());
    }

    //"Get Events After" Tests
    @Test
    public void getEventsAfter_getAfterFirstEvent_returnsEmptyList() {
        BoardCreationEvent event = new BoardCreationEvent();
        eventHistory.addEvent(event);
        List<GridEvent> events = eventHistory.getEventsAfter(event.getMillis());
        assertEquals(0, events.size());
    }

    @Test
    public void getEventsAfter_getAfterEvent7Of10_returnsListSize3() throws InterruptedException {
        long millis = 0;
        for (int i = 0; i < 10; i++) {
            eventHistory.addEvent(new BoardCreationEvent());
            if (i == 6) {
                millis = eventHistory.getEventsAfter(0).get(6).getMillis();
                sleep(1);
            }
        }
        List<GridEvent> events = eventHistory.getEventsAfter(millis);
        assertEquals(3, events.size());
    }

    //TODO: "Set off timer" tests

}