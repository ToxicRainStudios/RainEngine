package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.Color;

public class NPC extends BaseNPC {

    public NPC(float startingXpos, float startingYpos, float rotation, float size) {
        super(startingXpos, startingYpos, rotation, size);

        GameFactory.npcManager.addNPC(this, behaviorSequence);
    }

    public boolean canSeePlayer() {
        return canSeeTarget(GameFactory.player.playerPos);
    }

    public void moveTowardsPlayer(float speed) {
        moveTowardsTarget(GameFactory.player.playerPos, speed);
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(TextureSystem.getTexture("playerTexture"), npcPos.x, npcPos.y, Constants.NPC_ZLEVEL,
                new TileParameters(rotation, 0f, 0f, 1f, 1f, Color.toFloatArray(Color.WHITE), null));
    }

    @Override
    public void update(float deltaTime) {
        // Example behavior: If can see player, move towards them
        if (canSeePlayer()) {
            moveTowardsPlayer(0.05f * deltaTime);
        }
    }
}
