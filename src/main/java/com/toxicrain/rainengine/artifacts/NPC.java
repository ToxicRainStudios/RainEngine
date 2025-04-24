package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.artifacts.behavior.BehaviorSequence;
import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.core.datatypes.Color;
import com.toxicrain.rainengine.texture.TextureSystem;
import lombok.Getter;
import lombok.Setter;


public class NPC implements IArtifact {
    public TilePos npcPos;

    @Getter @Setter
    private float directionX; // Direction vector X
    @Getter @Setter
    private float directionY; // Direction vector Y
    float rotation; // Rotation angle in radians

    private float fieldOfViewAngle; // Vision cone angle in degrees
    private float visionDistance;   // Max distance NPC can see
    private boolean playerInSight;  // If the player is within the vision cone
    float size;
    private AABB npcAABB;
    @Getter @Setter
    BehaviorSequence behaviorSequence;


    public NPC(float startingXpos, float startingYpos, float rotation, float size) {
        this.npcPos = new TilePos(startingXpos,startingYpos, 1);

        this.directionX = (float) Math.cos(rotation);
        this.directionY = (float) Math.sin(rotation);
        this.rotation = rotation; // Set initial rotation
        this.fieldOfViewAngle = 90f;  // Example 90-degree FOV
        this.visionDistance = 300f;   // Max distance the NPC can see
        this.size = size;
        GameFactory.npcManager.addNPC(this, behaviorSequence);

        float halfSize = this.size / 2.0f;

        this.npcAABB = new AABB(
                npcPos.x - halfSize, // minX
                npcPos.y - halfSize, // minY
                npcPos.x + halfSize, // maxX
                npcPos.y + halfSize  // maxY
        );

    }

    public boolean canSeePlayer() {
        // Calculate the direction to the player
        float deltaX = GameFactory.player.playerPos.x  - this.npcPos.x;
        float deltaY = GameFactory.player.playerPos.y  - this.npcPos.y;

        // Distance to the player
        float distanceToPlayer = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distanceToPlayer > visionDistance) {
            return false;  // Player is too far away to be seen
        }

        // Normalize direction to the player
        float directionToPlayerX = deltaX / distanceToPlayer;
        float directionToPlayerY = deltaY / distanceToPlayer;

        // Calculate NPC's current direction based on its rotation
        float npcDirectionX = (float) Math.cos(this.rotation);
        float npcDirectionY = (float) Math.sin(this.rotation);

        // Dot product between the NPC's current direction and the direction to the player
        float dotProduct = npcDirectionX * directionToPlayerX + npcDirectionY * directionToPlayerY;

        // Clamp dot product to the range [-1, 1] to avoid NaN in acos
        dotProduct = Math.max(-1, Math.min(1, dotProduct));

        // Calculate the angle between the two directions
        float angle = (float) Math.acos(dotProduct);

        // Convert field of view angle from degrees to radians for comparison
        float fovInRadians = (float) Math.toRadians(fieldOfViewAngle / 2);

        // Check if the angle is within the field of view
        return angle <= fovInRadians;
    }

    // Method to set the NPC's rotation
    public void lookAt(float angle) {
        this.rotation = angle; // Update rotation
    }

    // Method to move towards the player gradually
    public void moveTowardsPlayer(float speed) {
        float deltaX = GameFactory.player.playerPos.x - this.npcPos.x;
        float deltaY = GameFactory.player.playerPos.y - this.npcPos.y;
        handleCollisions();

        // Normalize direction
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) { // Prevent division by zero
            this.npcPos.x += (deltaX / distance) * speed; // Update position smoothly
            this.npcPos.y += (deltaY / distance) * speed;
        }
    }

    // Render the NPC using BatchRenderer
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(TextureSystem.getTexture("playerTexture"), this.npcPos.x, this.npcPos.y, Constants.npcZLevel,
                new TileParameters(this.rotation, 0f,0f, 1f,1f, Color.toFloatArray(Color.WHITE), null));
    }

    void handleCollisions() {
        float halfSize = this.size / 2.0f;

        // Update npc's AABB based on its position and size
         this.npcAABB.update(
                 npcPos.x - halfSize,
                 npcPos.y - halfSize,
                 npcPos.x + halfSize,
                 npcPos.y + halfSize
        );

        char collisionDirection = Collisions.collideWorld(this.npcAABB);

        // Handle the collision direction with a switch statement
        switch (collisionDirection) {
            case 'u':
                // Colliding from below
                this.npcPos.y += 0.002f;
                break;
            case 'd':
                // Colliding from above
                this.npcPos.y -= 0.002f;
                break;
            case 'l':
                // Colliding from the left
                this.npcPos.x += 0.002f;
                break;
            case 'r':
                // Colliding from the right
                this.npcPos.y -= 0.002f;
                break;
        }
    }
}

