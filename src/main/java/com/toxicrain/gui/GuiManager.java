package com.toxicrain.gui;

import com.toxicrain.core.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;


/**
 * Controls what GUI renders when
 *
 * @author strubium
 */
public class GuiManager {
    // Map of GUI names to their render functions
    private final Map<String, Consumer<Void>> guiScreens = new HashMap<>();
    private final Set<String> activeGUIs = new CopyOnWriteArraySet<>();

    // Registers a GUI screen with a unique name
    public void registerGUI(String name, Consumer<Void> renderFunction) {
        guiScreens.put(name, renderFunction);
    }

    // Adds a GUI to the set of active GUIs to be rendered
    public void addActiveGUI(String name) {
        if (guiScreens.containsKey(name)) {
            activeGUIs.add(name);
        } else {
            Logger.printERROR("GUI screen not found: " + name);
        }
    }

    // Removes a GUI from the set of active GUIs
    public void removeActiveGUI(String name) {
        activeGUIs.remove(name);
    }

    // Clears all active GUIs, stopping any GUI rendering
    public void clearActiveGUIs() {
        activeGUIs.clear();
    }

    // Renders all active GUIs
    public void render() {
        for (String guiName : activeGUIs) {
            if (guiScreens.containsKey(guiName)) {
                guiScreens.get(guiName).accept(null);
            } else {
                Logger.printERROR("GUI screen not found during render: " + guiName);
            }
        }
    }
}

