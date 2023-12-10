package edu.unh.cs.cs619.bulletzone.model;

import android.widget.Button;

import java.util.Map;

/**
 * State implementation for controlling builders.
 */
public class BuilderState implements State{

    private Map<String, Button> buttons;

    /**
     * Constructor. Acts as a state transition to BuilderState
     * @param buttons Buttons used in the UI.
     */
    public BuilderState(Map<String, Button> buttons) {
        this.buttons = buttons;
        //disable eject
        buttons.get("EJECT").setEnabled(false);
        //enable builder actions
        buttons.get("BUILD_WALL").setEnabled(true);
        buttons.get("BUILD_ROAD").setEnabled(true);
        buttons.get("BUILD_DECK").setEnabled(true);
        buttons.get("DISMANTLE").setEnabled(true);
    }

    @Override
    public Map<String, Button> getButtons() {
        return buttons;
    }
}
