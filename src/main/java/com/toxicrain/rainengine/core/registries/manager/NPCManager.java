package com.toxicrain.rainengine.core.registries.manager;

import com.toxicrain.rainengine.artifacts.NPC;
import com.toxicrain.rainengine.artifacts.behavior.BehaviorSequence;
import com.toxicrain.rainengine.core.render.BatchRenderer;

import java.util.ArrayList;
import java.util.List;

public class NPCManager {

    private final List<NPC> npcs;

    public NPCManager() {
        npcs = new ArrayList<>();
    }

    // Add a new NPC to the manager along with its BehaviorSequence
    public void addNPC(NPC npc, BehaviorSequence behaviorSequence) {
        npc.setBehaviorSequence(behaviorSequence);  // Associate the behavior sequence with the NPC
        npcs.add(npc);
    }

    // Update all NPCs and their behaviors
    public void update(double deltaTime) {
        // Loop through all NPCs and update their behavior
        for (NPC npc : npcs) {
            // Execute the NPC's behavior sequence if it's defined
            BehaviorSequence behaviorSequence = npc.getBehaviorSequence();
            if (behaviorSequence != null) {
                behaviorSequence.execute(npc);  // Execute the behavior sequence
            }
        }
    }

    // Render all NPCs
    public void render(BatchRenderer batchRenderer) {
        for (NPC npc : npcs) {
            npc.render(batchRenderer);
        }
    }

    // Get all NPCs (if needed)
    public List<NPC> getNPCs() {
        return npcs;
    }

    // Remove an NPC from the manager
    public void removeNPC(NPC npc) {
        npcs.remove(npc);
    }
}
