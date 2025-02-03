package com.toxicrain.artifacts;

import com.toxicrain.factories.GameFactory;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.Color;
import lombok.Getter;

public class Projectile implements IArtifact {
    @Getter
    private float x, y;
    private final float velocityX, velocityY;
    @Getter
    private float lifeTime;
    private final TextureInfo texture;


    public Projectile(float xpos, float ypos, float veloX, float veloY, TextureInfo texture) {
        this.x = xpos;
        this.y = ypos;
        this.velocityX = veloX;
        this.velocityY = veloY;
        this.texture = texture;
        GameFactory.projectileManager.addProjectile(this);
    }

    public void update() {
        this.x += this.velocityX;
        this.y += this.velocityY;
        this.lifeTime += 0.0001f;
    }

    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexturePos(this.texture, this.x, this.y, 1.02f, velocityX, velocityY,1, 1, Color.toFloatArray(Color.WHITE));
    }
}
