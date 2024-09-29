package com.toxicrain.artifacts;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;
import lombok.Getter;
import lombok.Setter;

import static com.toxicrain.util.TextureUtils.playerTexture;

public class NPC {
    @Getter @Setter
    private float X; // Current X position
    @Getter @Setter
    private float Y; // Current Y position
    @Getter @Setter
    private float directionX; // Direction vector X
    @Getter @Setter
    private float directionY; // Direction vector Y
    private float rotation; // Rotation angle in radians

    private float fieldOfViewAngle; // Vision cone angle in degrees
    private float visionDistance;   // Max distance NPC can see
    private boolean playerInSight;  // If the player is within the vision cone

    public NPC(float startingXpos, float startingYpos, float rotation) {
        this.X = startingXpos;
        this.Y = startingYpos;
        this.directionX = (float) Math.cos(rotation);
        this.directionY = (float) Math.sin(rotation);
        this.rotation = rotation; // Set initial rotation
        this.fieldOfViewAngle = 90f;  // Example 90-degree FOV
        this.visionDistance = 300f;   // Max distance the NPC can see
    }

    public boolean canSeePlayer() {
        // Calculate the direction to the player
        float deltaX = Player.posX - this.X;
        float deltaY = Player.posY - this.Y;

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
        float deltaX = Player.posX - this.X;
        float deltaY = Player.posY - this.Y;

        // Normalize direction
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) { // Prevent division by zero
            this.X += (deltaX / distance) * speed; // Update position smoothly
            this.Y += (deltaY / distance) * speed;
        }
    }

    // Render the NPC using BatchRenderer
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(playerTexture, this.X, this.Y, 1.01f,
                this.rotation, 1, 1, Color.toFloatArray(Color.WHITE));
    }
}
