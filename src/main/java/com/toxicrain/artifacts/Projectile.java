package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

public class Projectile implements IArtifact {
    private float x, y;
    private float velocityX, velocityY;


    public Projectile(float xpos, float ypos, float veloX, float veloY) {
        x = xpos;
        y = ypos;
        velocityX = veloX;
        velocityY = veloY;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public static void render(BatchRenderer batchRenderer, Projectile projectile, TextureInfo textureInfo) {
        batchRenderer.addTexture(textureInfo, projectile.x, projectile.y, 1.1f, 0, Color.toFloatArray(1.0f, Color.WHITE));
    }
}
