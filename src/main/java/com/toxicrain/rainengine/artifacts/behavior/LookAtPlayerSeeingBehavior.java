package com.toxicrain.rainengine.artifacts.behavior;

import com.toxicrain.rainengine.artifacts.NPC;
import com.toxicrain.rainengine.factories.GameFactory;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LookAtPlayerSeeingBehavior extends Behavior {

    @Override
    public boolean execute(NPC npc) {
        if (npc.canSeePlayer()) {
            // Get the current position of the NPC and the Player
            float npcX = npc.npcPos.x;
            float npcY = npc.npcPos.y;
            float playerX = GameFactory.player.playerPos.x;
            float playerY = GameFactory.player.playerPos.y;

            // Calculate the direction to the player
            float deltaX = playerX - npcX;
            float deltaY = playerY - npcY;

            // Calculate the angle (in radians) to rotate towards the player
            float angle = (float) Math.atan2(deltaY, deltaX);

            // Set the NPC's rotation or facing direction
            npc.lookAt(angle);

            return true; // Indicates that the behavior executed successfully
        }
        return false; // Indicates no movement required
    }
}
