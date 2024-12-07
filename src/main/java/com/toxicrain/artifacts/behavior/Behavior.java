package com.toxicrain.artifacts.behavior;

import com.toxicrain.artifacts.NPC;

/**
 * Base class for behavior trees
 */
public abstract class Behavior {
    public abstract boolean execute(NPC npc);
}