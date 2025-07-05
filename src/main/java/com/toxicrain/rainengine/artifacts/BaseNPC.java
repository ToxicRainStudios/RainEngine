package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.artifacts.behavior.BehaviorSequence;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.datatypes.Color;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.texture.TextureSystem;
import lombok.Getter;
import lombok.Setter;

public class BaseNPC implements IArtifact {

    @Getter @Setter
    protected TilePos npcPos;

    @Getter @Setter
    protected Vector2 direction;

    @Getter @Setter
    protected float rotation;

    @Getter @Setter
    protected float fieldOfViewAngle = 90f;

    @Getter @Setter
    protected float visionDistance = 300f;

    @Getter @Setter
    protected float size;

    @Getter @Setter
    protected BehaviorSequence behaviorSequence;

    protected AABB npcAABB;

    public BaseNPC(float startingXpos, float startingYpos, float rotation, float size) {
        this.npcPos = new TilePos(startingXpos, startingYpos, 1);
        this.rotation = rotation;
        this.size = size;

        this.direction = new Vector2((float) Math.cos(rotation), (float) Math.sin(rotation));

        float halfSize = size / 2f;
        this.npcAABB = new AABB(
                npcPos.x - halfSize,
                npcPos.y - halfSize,
                npcPos.x + halfSize,
                npcPos.y + halfSize
        );
    }

    public boolean canSeeTarget(TilePos targetPos) {
        float deltaX = targetPos.x - npcPos.x;
        float deltaY = targetPos.y - npcPos.y;

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > visionDistance) return false;

        Vector2 directionToTarget = new Vector2(deltaX / distance, deltaY / distance);
        Vector2 npcDirection = new Vector2((float) Math.cos(rotation), (float) Math.sin(rotation));

        float dotProduct = npcDirection.x * directionToTarget.x + npcDirection.y * directionToTarget.y;
        dotProduct = Math.max(-1, Math.min(1, dotProduct));

        float angle = (float) Math.acos(dotProduct);
        float fovInRadians = (float) Math.toRadians(fieldOfViewAngle / 2);

        return angle <= fovInRadians;
    }

    public void moveTowardsTarget(TilePos targetPos, float speed) {
        float deltaX = targetPos.x - npcPos.x;
        float deltaY = targetPos.y - npcPos.y;

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            npcPos.x += (deltaX / distance) * speed;
            npcPos.y += (deltaY / distance) * speed;
        }

        handleCollisions();
    }

    public void lookAt(float angle) {
        this.rotation = angle;
        this.direction.set((float) Math.cos(angle), (float) Math.sin(angle));
    }

    public void handleCollisions() {
        float halfSize = this.size / 2f;

        npcAABB.update(
                npcPos.x - halfSize,
                npcPos.y - halfSize,
                npcPos.x + halfSize,
                npcPos.y + halfSize
        );

        char collisionDirection = Collisions.collideWorld(npcAABB);

        switch (collisionDirection) {
            case 'u':
                npcPos.y += 0.002f;
                break;
            case 'd':
                npcPos.y -= 0.002f;
                break;
            case 'l':
                npcPos.x += 0.002f;
                break;
            case 'r':
                npcPos.x -= 0.002f;
                break;
        }
    }

    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(TextureSystem.getTexture("npcTexture"), npcPos.x, npcPos.y, Constants.NPC_ZLEVEL,
                new TileParameters(rotation, 0f, 0f, 1f, 1f, Color.toFloatArray(Color.WHITE), null));
    }

    // Optional hooks for specialized NPCs
    public void update(float deltaTime) {}
}
