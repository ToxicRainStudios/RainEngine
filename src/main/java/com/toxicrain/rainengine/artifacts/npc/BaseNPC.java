package com.toxicrain.rainengine.artifacts.npc;

import com.toxicrain.rainengine.artifacts.RenderableArtifact;
import com.toxicrain.rainengine.artifacts.behavior.BehaviorSequence;
import com.toxicrain.rainengine.core.datatypes.*;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;

import lombok.Getter;
import lombok.Setter;

public class BaseNPC extends RenderableArtifact implements IArtifact {

    @Getter @Setter protected Vector2 direction;
    @Getter @Setter protected float fieldOfViewAngle = 90f;
    @Getter @Setter protected float visionDistance = 300f;
    @Getter @Setter protected BehaviorSequence behaviorSequence;

    protected AABB npcAABB;

    public BaseNPC(Resource imageLocation, float startingXpos, float startingYpos, float rotation, float size) {
        super(imageLocation, startingXpos, startingYpos, rotation, size);

        this.direction = new Vector2((float) Math.cos(rotation), (float) Math.sin(rotation));

        float halfSize = size / 2f;
        this.npcAABB = new AABB(
                position.x - halfSize,
                position.y - halfSize,
                position.x + halfSize,
                position.y + halfSize
        );
    }

    public boolean canSeeTarget(TilePos targetPos) {
        float deltaX = targetPos.x - position.x;
        float deltaY = targetPos.y - position.y;

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
        float deltaX = targetPos.x - position.x;
        float deltaY = targetPos.y - position.y;

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            position.x += (deltaX / distance) * speed;
            position.y += (deltaY / distance) * speed;
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
                position.x - halfSize,
                position.y - halfSize,
                position.x + halfSize,
                position.y + halfSize
        );

        char collisionDirection = Collisions.collideWorld(npcAABB);

        switch (collisionDirection) {
            case 'u':
                position.y += 0.002f;
                break;
            case 'd':
                position.y -= 0.002f;
                break;
            case 'l':
                position.x += 0.002f;
                break;
            case 'r':
                position.x -= 0.002f;
                break;
        }
    }

    // Optional hooks for specialized NPCs
    public void update(float deltaTime) {}
}
