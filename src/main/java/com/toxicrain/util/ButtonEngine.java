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
        this.x = x;
        this.y = y;
        this.width = TextEngine.getTextWidth(label);  // Set an appropriate width for the button
        this.height = 500;  // Set an appropriate height for the button
        this.clicked = false;
        this.mouseOver = false;
        this.label = label;
    }

    public void update(float mouseX, float mouseY, boolean mouseClick) {
        // Check if the mouse is over the button
        mouseOver = isMouseOver(mouseX, mouseY);

        // Handle mouse click events
        if (mouseOver && mouseClick) {
            clicked = true;  // Button is considered clicked
        } else if (!mouseClick) {
            clicked = false; // Reset clicked when mouse is released
        }
    }

    public boolean isClicked() {
        return clicked;
    }

    public void render(BatchRenderer batchRenderer) {
        // Render button text
        textEngine.render(batchRenderer, this.label, (int) this.x, (int) this.y);
    }

    private boolean isMouseOver(float mouseX, float mouseY) {
        boolean isOver = mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);

        // Debug prints
        System.out.println("Mouse Position: (" + mouseX + ", " + mouseY + ")");
        System.out.println("Button Position: (" + x + ", " + y + ")");
        System.out.println("Button Size: (Width: " + width + ", Height: " + height + ")");
        System.out.println("Is Mouse Over Button: " + isOver);

        return isOver;
    }

}
