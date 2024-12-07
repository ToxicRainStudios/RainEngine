package com.toxicrain.artifacts.behavior;

import com.toxicrain.artifacts.NPC;

/**
 * Composite class to manage a sequence of behaviors
 */
public class BehaviorSequence extends Behavior {
    private final Behavior[] behaviors;

    public BehaviorSequence(Behavior... behaviors) {
        this.behaviors = behaviors;
    }

    @Override
    public boolean execute(NPC npc) {
        for (Behavior behavior : behaviors) {
            if (!behavior.execute(npc)) {
                return false; // If any behavior fails, stop execution
            }
        }
        return true; // All behaviors executed successfully
    }
}
