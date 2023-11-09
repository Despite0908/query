package edu.unh.cs.cs619.bulletzone.model.ServerEvents;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import static java.lang.Thread.sleep;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class EventHistoryTest {

    @InjectMocks
    EventHistory eventHistory;

    @Mock
    Clock mockClock;

    @Before
    public void setup() {
        eventHistory = EventHistory.start(mockClock);
        eventHistory.clear();
    }

    //Basic List Tests
    @Test
    public void getEventsAfter_nothingAdded_returnsEmptyList() {
        assertEquals(eventHistory.getEventsAfter(0).size(), 0);
    }

    @Test
    public void getEventsAfter_addEvent_returnsListSize1SameEvent() {
        when(mockClock.millis()).thenReturn(1L);
        BoardCreationEvent event = new BoardCreationEvent();
        eventHistory.addEvent(event);
        List<GridEvent> events = eventHistory.getEventsAfter(0);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    public void getEventsAfter_add10Event_returnsListSize10() {
        for (int i = 0; i < 10; i++) {
            when(mockClock.millis()).thenReturn((long)i + 1);
            eventHistory.addEvent(new BoardCreationEvent());
        }
        List<GridEvent> events = eventHistory.getEventsAfter(0);
        assertEquals(10, events.size());
    }

    //"Get Events After" Tests
    @Test
    public void getEventsAfter_getAfterFirstEvent_returnsEmptyList() {
        when(mockClock.millis()).thenReturn(1L);
        BoardCreationEvent event = new BoardCreationEvent();
        eventHistory.addEvent(event);
        List<GridEvent> events = eventHistory.getEventsAfter(event.getMillis());
        assertEquals(0, events.size());
    }

    @Test
    public void getEventsAfter_getAfterEvent7Of10_returnsListSize3() throws InterruptedException {
        long millis = 0;
        for (int i = 0; i < 10; i++) {
            when(mockClock.millis()).thenReturn((long) i + 1);
            eventHistory.addEvent(new BoardCreationEvent());
            if (i == 6) {
                when(mockClock.millis()).thenReturn(7L);
                millis = eventHistory.getEventsAfter(0).get(6).getMillis();
                sleep(1);
            }
        }
        List<GridEvent> events = eventHistory.getEventsAfter(millis);
        assertEquals(3, events.size());
    }

    //Tests to prune old events from list
    @Test
    public void getEventsAfter_addEventWait_returnsEmptyListPrunesItem() {
        when(mockClock.millis()).thenReturn(1L);
        BoardCreationEvent event = new BoardCreationEvent();
        eventHistory.addEvent(event);
        List<GridEvent> events = eventHistory.getEventsAfter(TimeUnit.MINUTES.toMillis(2) + 1);
        assertEquals(0, events.size());
        assertEquals(0, eventHistory.getEventsAfter(0).size());
    }

    @Test
    public void getEventsAfter_getAfterEvent7Of10Wait_returnsListSize0Prunes7Items() throws InterruptedException {
        long millis = 0;
        for (int i = 0; i < 10; i++) {
            when(mockClock.millis()).thenReturn((long) i + 1);
            eventHistory.addEvent(new BoardCreationEvent());
            if (i == 6) {
                when(mockClock.millis()).thenReturn(7L);
                millis = eventHistory.getEventsAfter(0).get(6).getMillis();
                sleep(1);
            }
        }
        List<GridEvent> events = eventHistory.getEventsAfter(TimeUnit.MINUTES.toMillis(2) + millis);
        assertEquals(0, events.size());
        assertEquals(3, eventHistory.getEventsAfter(0).size());
    }
}