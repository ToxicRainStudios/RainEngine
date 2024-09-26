package com.toxicrain.artifacts;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;
import lombok.Getter;
import lombok.Setter;

import static com.toxicrain.util.TextureUtils.playerTexture;

public class NPC {
    @Getter @Setter
    private float X;
    @Getter @Setter
    private float Y;
    @Getter @Setter
    private float directionX;
    @Getter @Setter
    private float directionY;
    @Getter @Setter
    private int aiType;

    // Constructor to initialize NPC
    public NPC(float startingXpos, float startingYpos, float rotation, int ai) {
        this.X = startingXpos;
        this.Y = startingYpos;
        this.directionX = (float) Math.cos(rotation);
        this.directionY = (float) Math.sin(rotation);
        this.aiType = ai;
    }

    // Method to run AI behavior based on aiType
    public void runAI() {
        switch (this.aiType) {
            case 1:
                // Basic following behavior
                followPlayer(3, 1); // Adjusted offsets for desired behavior
                updateDirection(); // Update direction to look at the player
                break;

            case 2:
                // Smooth approach to player
                moveTowardsPlayer(0.002f);
                updateDirection(); // Update direction to look at the player
                break;

            default:
                // Default behavior (could be idle, patrol, etc.)
                break;
        }
    }

    // Method to follow the player with a specified offset
    private void followPlayer(float offsetX, float offsetY) {
        this.X = Player.posX - offsetX;
        this.Y = Player.posY - offsetY;
    }

    // Method to move towards the player gradually
    private void moveTowardsPlayer(float speed) {
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
    private void updateDirection() {
        float deltaX = Player.posX - this.X;
        float deltaY = Player.posY - this.Y;

        float angle = (float) Math.atan2(deltaY, deltaX); // Calculate angle to the player
        this.directionX = (float) Math.cos(angle); // Update directionX
        this.directionY = (float) Math.sin(angle); // Update directionY
    }

    // Render the NPC using BatchRenderer
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexturePos(playerTexture, this.X, this.Y, 1.01f,
                this.directionX, this.directionY, 1, 1, Color.toFloatArray(Color.WHITE));
    }
}
