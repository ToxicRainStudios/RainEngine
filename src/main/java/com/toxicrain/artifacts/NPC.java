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
    @Getter @Setter
    private int aiType; // AI type
    private float rotation; // Rotation angle in radians

    // Constructor to initialize NPC
    public NPC(float startingXpos, float startingYpos, float rotation, int ai) {
        this.X = startingXpos;
        this.Y = startingYpos;
        this.directionX = (float) Math.cos(rotation);
        this.directionY = (float) Math.sin(rotation);
        this.aiType = ai;
        this.rotation = rotation; // Set initial rotation
    }

    // Method to set the NPC's rotation
    public void lookAt(float angle) {
        this.rotation = angle; // Update rotation
    }

    // Method to follow the player with a specified offset
    public void followPlayer(float offsetX, float offsetY) {
        this.X = Player.posX - offsetX;
        this.Y = Player.posY - offsetY;
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

    // Method to update the direction of the NPC to look at the player
    public void updateDirection() {
        float deltaX = Player.posX - this.X;
        float deltaY = Player.posY - this.Y;

        float angle = (float) Math.atan2(deltaY, deltaX); // Calculate angle to the player
        this.directionX = (float) Math.cos(angle); // Update directionX
        this.directionY = (float) Math.sin(angle); // Update directionY
    }

    // Render the NPC using BatchRenderer
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(playerTexture, this.X, this.Y, 1.01f,
                this.rotation, 1, 1, Color.toFloatArray(Color.WHITE));
    }
}
