package com.toxicrain.util;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.Logger;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ButtonEngine {
    private TextEngine textEngine;
    private float x, y, width, height;
    private boolean clicked;

    public ButtonEngine(String label, float x, float y) {
        this.textEngine = new TextEngine(label, (int) x, (int) y);
        this.x = x;
        this.y = y;
        this.width = 300;  // Set an appropriate width for the button
        this.height = 300;  // Set an appropriate height for the button
        this.clicked = false;
    }

    public void update(float mouseX, float mouseY, boolean mouseClick) {
        if (isMouseOver(mouseX, mouseY) && mouseClick) {
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    public boolean isClicked() {
        return clicked;
    }

    public void render(BatchRenderer batchRenderer) {
        float scale = Player.cameraZ / 30;
        // Render button background (e.g., using a solid color or texture)
        // Assuming a texture for the button background
        TextureInfo buttonTexture = TextureUtils.playerTexture;
        batchRenderer.addTexture(buttonTexture, x, y, width, height, scale, scale, Color.toFloatArray(Color.LIGHT_GRAY));

        // Render button text
        textEngine.render(batchRenderer);
    }

    private boolean isMouseOver(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height);
    }
}
