package com.toxicrain.rainengine.artifacts.trigger;

import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.registries.manager.TriggerManager;
import io.reactivex.rxjava3.functions.Supplier;
import lombok.Getter;
import org.joml.Vector3f;

public class Trigger implements IArtifact {
    @Getter private final AABB bounds;
    private final Runnable action;
    @Getter private boolean triggered = false;
    private boolean oneTime;
    Supplier<Vector3f> playerPosSupplier;

    public Trigger(AABB bounds, Runnable action, boolean oneTime, Supplier<Vector3f> playerPosSupplier ) {
        this.bounds = bounds;
        this.action = action;
        this.oneTime = oneTime;
        this.playerPosSupplier = playerPosSupplier;

        TriggerManager.getInstance().addTrigger(this);
    }

    @Override
    public void update(double deltaTime) {
        try {
            if (!triggered && bounds.contains(playerPosSupplier.get())) {
                action.run();
                if (oneTime) triggered = true;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        triggered = false;
    }

}
