package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.core.Color;
import lombok.Getter;

public class Projectile implements IArtifact {
    @Getter
    private TilePos pos;
    private final float velocityX, velocityY;
    @Getter
    private float lifeTime;
    private final TextureInfo texture;


    public Projectile(float xpos, float ypos, float veloX, float veloY, TextureInfo texture) {
        this.pos = new TilePos(xpos, ypos, 1.02f);
        this.velocityX = veloX;
        this.velocityY = veloY;
        this.texture = texture;

        GameFactory.projectileManager.addProjectile(this);
    }

    public void update() {
        this.pos.x += this.velocityX;
        this.pos.y += this.velocityY;
        this.lifeTime += 0.0001f;
    }

    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(this.texture, this.pos.x, this.pos.y, this.pos.z, new TileParameters(null, velocityX, velocityY, 1,1, null, LightSystem.getLightSources()));
    }
}
