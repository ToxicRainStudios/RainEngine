package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

public class Projectile implements IArtifact {
    private static float x, y;
    private static float velocityX, velocityY;
    private static TextureInfo textureInfo;

    public Projectile(float x, float y, float velocityX, float velocityY, TextureInfo textureInfo) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.textureInfo = textureInfo;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public static void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(textureInfo, x, y, 1.1f, 0, Color.toFloatArray(1.0f, Color.WHITE));
    }
}
