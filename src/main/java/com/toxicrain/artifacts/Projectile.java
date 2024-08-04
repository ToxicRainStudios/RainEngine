package com.toxicrain.artifacts;

import com.toxicrain.core.Logger;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

public class Projectile implements IArtifact {
    private float x, y;
    private float velocityX, velocityY;
    private TextureInfo texture;


    public Projectile(float xpos, float ypos, float veloX, float veloY, TextureInfo texture) {
        this.x = xpos;
        this.y = ypos;
        this.velocityX = veloX;
        this.velocityY = veloY;
        this.texture = texture;
    }

    public void update() {
        this.x += this.velocityX;
        this.y += this.velocityY;
    }

    public static void render(BatchRenderer batchRenderer, Projectile projectile) {
        batchRenderer.addTexture(projectile.texture, projectile.x, projectile.y, 1.01f, 0, 1,1, Color.toFloatArray(Color.WHITE));
    }
}
