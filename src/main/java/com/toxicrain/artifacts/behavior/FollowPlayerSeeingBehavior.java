package com.toxicrain.artifacts.behavior;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;

public class FollowPlayerSeeingBehavior extends Behavior {
    private final float followDistance;

    public FollowPlayerSeeingBehavior(float followDistance) {
        this.followDistance = followDistance;
    }

    @Override
    public boolean execute(NPC npc) {
        if (npc.canSeePlayer()) {
            // Get the current position of the NPC and the Player
            float deltaX = Player.posX - npc.getX();
            float deltaY = Player.posY - npc.getY();
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // If the NPC is too far from the Player, move towards them
            if (distance > followDistance) {
                npc.moveTowardsPlayer(0.002f);
                return true; // Indicates that the behavior executed successfully
            }

        }
        return false; // Indicates no movement required
    }
}
