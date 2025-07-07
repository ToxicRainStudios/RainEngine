package com.toxicrain.rainengine.artifacts.behavior;

import com.toxicrain.rainengine.artifacts.BaseNPC;
import com.toxicrain.rainengine.artifacts.NPC;

/**
 * Base class for behavior trees
 */
public abstract class Behavior {
    public abstract boolean execute(NPC npc);
}