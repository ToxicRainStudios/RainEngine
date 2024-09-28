package com.toxicrain.artifacts.behavior;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;

public class BehaviorFollowPlayerSeeing extends Behavior {
    private final Player player;

    public BehaviorFollowPlayerSeeing(Player player) {
        this.player = player;
    }

    @Override
    public boolean execute(NPC npc) {
        if (npc.canSeePlayer()) {
            // Get the current position of the NPC and the Player
            float npcX = npc.getX();
            float npcY = npc.getY();
            float playerX = Player.posX;
            float playerY = Player.posY;

            // Calculate the direction to the player
            float deltaX = playerX - npcX;
            float deltaY = playerY - npcY;

            // Calculate the angle (in radians) to rotate towards the player
            float angle = (float) Math.atan2(deltaY, deltaX);

            // Set the NPC's rotation or facing direction
            npc.lookAt(angle); // Implement lookAt method in NPC class

            return true; // Indicates that the behavior executed successfully
        } else {
            return false;
        }
    }
}
