package edu.unh.cs.cs619.bulletzone.events;

public class CustomEvent {
    private CustomEventTypes eventType;
    private Object myObject;

    public CustomEvent(CustomEventTypes theEventType, Object customObject) {
        setEventType(theEventType);
        setMyObject(customObject);
    }

    public CustomEventTypes getEventType(){
        return eventType;
    }

    public void setEventType(CustomEventTypes passedEventType){
        this.eventType = passedEventType;
    }

    public void setMyObject(Object passedObject) {
        this.myObject = passedObject;
    }

    public Object getMyObject() {
        return myObject;
    }
}
