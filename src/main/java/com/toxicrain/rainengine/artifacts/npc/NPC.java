package com.toxicrain.rainengine.artifacts.npc;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.registries.manager.NPCManager;
import com.toxicrain.rainengine.factories.GameFactory;

public class NPC extends BaseNPC {

    public NPC(float startingXpos, float startingYpos, float rotation) {
        super(new Resource("npcTexture"), startingXpos, startingYpos, rotation);

        NPCManager.getInstance().addNPC(this, behaviorSequence);
    }

    public boolean canSeePlayer() {
        return canSeeTarget(GameFactory.player.getPosition());
    }

    public void moveTowardsPlayer(float speed) {
        moveTowardsTarget(GameFactory.player.getPosition(), speed);
    }

    @Override
    public void update(float deltaTime) {
        // Example behavior: If can see player, move towards them
        if (canSeePlayer()) {
            moveTowardsPlayer(0.05f * deltaTime);
        }
    }
}
