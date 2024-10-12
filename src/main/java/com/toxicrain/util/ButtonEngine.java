package com.toxicrain.util;

import com.toxicrain.core.Color;
import com.toxicrain.core.Logger;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.texture.TextureSystem;

public class ButtonEngine {
    private TextEngine textEngine;
    private float x, y, width, height;
    private boolean clicked;      // Whether the button was clicked
    private boolean mouseOver;    // Whether the mouse is currently over the button
    private String label;

    public ButtonEngine(TextEngine textEngine, String label, float x, float y) {
        this.textEngine = textEngine;
        this.x = x; // Center of the button
        this.y = y; // Center of the button
        this.width = TextEngine.getTextWidth(label);  // Set an appropriate width for the button
        this.height = 500;  // Set an appropriate height for the button
        this.clicked = false;
        this.mouseOver = false;
        this.label = label;
    }

    public void update(float mouseX, float mouseY, boolean mouseClick, float screenWidth, float screenHeight) {
        // Adjust the mouse coordinates to match the button's coordinate system
        float adjustedMouseX = mouseX - (screenWidth / 2);  // Centering X
        float adjustedMouseY = (screenHeight / 2) - mouseY; // Flipping Y

        // Debug prints for actual screen dimensions and original mouse position
        System.out.println("Screen Width: " + screenWidth + ", Screen Height: " + screenHeight);
        System.out.println("Original Mouse Position: (" + mouseX + ", " + mouseY + ")");

        // Check if the mouse is over the button
        mouseOver = isMouseOver(adjustedMouseX, adjustedMouseY);

        // Handle mouse click events
        if (mouseOver && mouseClick) {
            clicked = true;  // Button is considered clicked
        } else if (!mouseClick) {
            clicked = false; // Reset clicked when mouse is released
        }

        // Debug prints for adjusted mouse position
        System.out.println("Adjusted Mouse Position: (" + adjustedMouseX + ", " + adjustedMouseY + ")");
    }

    public boolean isClicked() {
        return clicked;
    }

    public void render(BatchRenderer batchRenderer) {
        // Render button text
        textEngine.render(batchRenderer, this.label, (int) this.x, (int) this.y);
    }

    private boolean isMouseOver(float mouseX, float mouseY) {
        // Calculate button boundaries based on its center position
        float buttonLeft = x - (width / 2);   // Left boundary
        float buttonRight = x + (width / 2);  // Right boundary
        float buttonTop = y + (height / 2);   // Top boundary
        float buttonBottom = y - (height / 2); // Bottom boundary

        // Debugging output for boundaries
        System.out.println("Calculated Boundaries:");
        System.out.println("Left: " + buttonLeft);
        System.out.println("Right: " + buttonRight);
        System.out.println("Top: " + buttonTop);
        System.out.println("Bottom: " + buttonBottom);

        // Check if the mouse is within the button boundaries
        boolean isOver = mouseX >= buttonLeft && mouseX <= buttonRight &&
                mouseY <= buttonTop && mouseY >= buttonBottom; // Adjust for inverted y-axis

        // Debug prints
        System.out.println("Mouse Position: (" + mouseX + ", " + mouseY + ")");
        System.out.println("Is Mouse Over Button: " + isOver);

        return isOver;
    }
}
