package edu.unh.cs.cs619.bulletzone.events;

public class CustomEvent {
    private String action;

    public CustomEvent(String passedString) {
        setAction (passedString);
    }

    public String getAction(){
        return action;
    }

    public void setAction(String action){
        this.action = action;
    }
}
