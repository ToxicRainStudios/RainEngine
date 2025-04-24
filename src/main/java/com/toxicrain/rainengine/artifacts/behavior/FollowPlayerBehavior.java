package com.toxicrain.rainengine.artifacts.behavior;

import com.toxicrain.rainengine.artifacts.NPC;
import com.toxicrain.rainengine.factories.GameFactory;

public class FollowPlayerBehavior extends Behavior {
    private final float followDistance; // Distance to maintain while following

    public FollowPlayerBehavior(float followDistance) {
        this.followDistance = followDistance;
    }


    @Override
    public boolean execute(NPC npc) {
        // Get the current position of the NPC and the Player
        float deltaX = GameFactory.player.playerPos.x - npc.npcPos.x;
        float deltaY = GameFactory.player.playerPos.y - npc.npcPos.y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // If the NPC is too far from the Player, move towards them
        if (distance > followDistance) {
            npc.moveTowardsPlayer(0.002f);
            return true; // Indicates that the behavior executed successfully
        }

        // If the NPC is close enough, do nothing or perform idle behavior
        return false; // Indicates no movement required
    }
}
