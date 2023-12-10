package edu.unh.cs.cs619.bulletzone.model;

import android.widget.Button;

import java.util.List;
import java.util.Map;

/**
 * State implementation for controlling Tanks
 */
public class TankState  implements State{
    private Map<String, Button> buttons;

    /**
     * Constructor. Acts as a state transition to TankState.
     * @param buttons Buttons used in the UI.
     */
    public TankState(Map<String, Button> buttons) {
        this.buttons = buttons;
        //disable builder actions
        buttons.get("BUILD_WALL").setEnabled(false);
        buttons.get("BUILD_ROAD").setEnabled(false);
        buttons.get("BUILD_DECK").setEnabled(false);
        buttons.get("DISMANTLE").setEnabled(false);
        //enable firing and ejecting
        buttons.get("EJECT").setEnabled(true);
    }

    @Override
    public Map<String, Button> getButtons() {
        return buttons;
    }
}
