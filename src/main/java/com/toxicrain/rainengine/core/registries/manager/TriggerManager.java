package com.toxicrain.rainengine.core.registries.manager;

import com.toxicrain.rainengine.artifacts.trigger.Trigger;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.logging.RainLogger;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager {
    private final List<Trigger> triggers;

    public TriggerManager() {
        triggers = new ArrayList<>();
    }

    public void clearTriggers(){
        triggers.clear();
    }

    public void addTrigger(Trigger trigger) {
        RainLogger.RAIN_LOGGER.info("Added Trigger: {}", trigger.getBounds().toString());
        triggers.add(trigger);
    }

    public void update(TilePos tilePos) {
        for (Trigger trigger : new ArrayList<>(triggers)) {
            trigger.check(tilePos);
        }
    }

    public void resetAll() {
        for (Trigger trigger : new ArrayList<>(triggers)) {
            trigger.reset();
        }
    }
}
