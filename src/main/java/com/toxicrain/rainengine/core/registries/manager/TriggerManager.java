package com.toxicrain.rainengine.core.registries.manager;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.artifacts.trigger.Trigger;
import com.toxicrain.rainengine.core.logging.RainLogger;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TriggerManager extends BaseInstanceable<TriggerManager> {
    private final List<Trigger> triggers;

    public static TriggerManager getInstance() {
        return BaseInstanceable.getInstance(TriggerManager.class);
    }

    private TriggerManager() {
        triggers = new ArrayList<>();
    }

    public void clearTriggers(){
        RainLogger.RAIN_LOGGER.debug("Clearing Triggers!");
        triggers.clear();
    }

    public void addTrigger(Trigger trigger) {
        RainLogger.RAIN_LOGGER.info("Added Trigger: {}", trigger.getBounds().toString());
        triggers.add(trigger);
    }

    public void update(double deltaTime) {
        for (Trigger trigger : new ArrayList<>(triggers)) {
            trigger.update(deltaTime);
        }
    }

    public void resetAll() {
        for (Trigger trigger : new ArrayList<>(triggers)) {
            trigger.reset();
        }
    }
}
