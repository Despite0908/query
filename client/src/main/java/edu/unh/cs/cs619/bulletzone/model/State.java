package edu.unh.cs.cs619.bulletzone.model;

import android.widget.Button;

import java.util.Map;

/**
 * State interface for Updating UI. In the future, should have a function to
 * enable/disable directional buttons based on terrain, improvements and entities.
 */
public interface State {

    public Map<String, Button> getButtons();
}
