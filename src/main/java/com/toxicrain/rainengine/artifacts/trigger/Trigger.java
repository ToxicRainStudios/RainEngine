package com.toxicrain.rainengine.artifacts.trigger;

import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.datatypes.vector.Vector3;
import com.toxicrain.rainengine.core.registries.manager.TriggerManager;
import lombok.Getter;

public class Trigger {
    @Getter private final AABB bounds;
    private final Runnable action;
    @Getter private boolean triggered = false;
    private boolean oneTime;

    public Trigger(AABB bounds, Runnable action, boolean oneTime) {
        this.bounds = bounds;
        this.action = action;
        this.oneTime = oneTime;

        TriggerManager.getInstance().addTrigger(this);
    }

    public void check(Vector3 playerBounds) {
        if (!triggered && bounds.contains(playerBounds)) {
            action.run();
            if (oneTime) triggered = true;
        }
    }

    public void reset() {
        triggered = false;
    }

}
