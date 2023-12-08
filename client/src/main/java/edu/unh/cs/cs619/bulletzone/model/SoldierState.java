package edu.unh.cs.cs619.bulletzone.model;

import android.widget.Button;

import java.util.Map;

/**
 * State implementation for controlling soldiers.
 */
public class SoldierState  implements State{

    private Map<String, Button> buttons;

    /**
     * Constructor. Acts as a state transition to SoldierState.
     * @param buttons Buttons used in the UI.
     */
    public SoldierState(Map<String, Button> buttons) {
        this.buttons = buttons;
        //Disable Builder actions and eject
        buttons.get("BUILD").setEnabled(false);
        buttons.get("DISMANTLE").setEnabled(false);
        buttons.get("EJECT").setEnabled(false);
    }

    @Override
    public Map<String, Button> getButtons() {
        return buttons;
    }
}
