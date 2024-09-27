package com.toxicrain.artifacts.behavior;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;

public class FollowPlayerBehavior extends Behavior {
    private final float followDistance; // Distance to maintain while following

    public FollowPlayerBehavior(float followDistance) {
        this.followDistance = followDistance;
    }

    @Override
    public boolean execute(NPC npc) {
        // Get the current position of the NPC and the Player
        float npcX = npc.getX();
        float npcY = npc.getY();
        float playerX = Player.posX;
        float playerY = Player.posY;

        // Calculate the distance between the NPC and the Player
        float deltaX = playerX - npcX;
        float deltaY = playerY - npcY;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // If the NPC is too far from the Player, move towards them
        if (distance > followDistance) {
            npc.moveTowards(playerX, playerY);
            return true; // Indicates that the behavior executed successfully
        }

        // If the NPC is close enough, do nothing or perform idle behavior
        return false; // Indicates no movement required
    }
}
