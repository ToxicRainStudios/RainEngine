package com.toxicrain.rainengine.artifacts.behavior;

import com.toxicrain.rainengine.artifacts.NPC;
import com.toxicrain.rainengine.factories.GameFactory;

public class FollowPlayerSeeingBehavior extends Behavior {
    private final float followDistance;

    public FollowPlayerSeeingBehavior(float followDistance) {
        this.followDistance = followDistance;
    }

    @Override
    public boolean execute(NPC npc) {
        if (npc.canSeePlayer()) {
            // Get the current position of the NPC and the Player
            float deltaX = GameFactory.player.playerPos.x - npc.getX();
            float deltaY = GameFactory.player.playerPos.y - npc.getY();
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
