package com.toxicrain.gui;

import com.toxicrain.core.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Controls what GUI renders when
 *
 * @author strubium
 */
public class GuiManager {
    // Map of GUI names to their render functions
    private final Map<String, Consumer<Void>> guiScreens = new HashMap<>();
    private String activeGUI = null; // Keeps track of the current active GUI

    // Registers a GUI screen with a unique name
    public void registerGUI(String name, Consumer<Void> renderFunction) {
        guiScreens.put(name, renderFunction);
    }

    // Sets the active GUI to be rendered
    public void setActiveGUI(String name) {
        if (guiScreens.containsKey(name)) {
            activeGUI = name;
        } else {
            Logger.printERROR("GUI screen not found: " + name);
        }
    }

    // Clears the active GUI, stopping any GUI rendering
    public void clearActiveGUI() {
        activeGUI = null;
    }

    // Renders the active GUI if it’s set
    public void render() {
        if (activeGUI != null && guiScreens.containsKey(activeGUI)) {
            guiScreens.get(activeGUI).accept(null);
        }
    }
}

