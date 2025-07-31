package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureRegion;
import lombok.Getter;

@Getter
public class Projectile extends RenderableArtifact implements IArtifact {

    private final Vector2 velocity;
    private float lifeTime;

    public Projectile(Resource imageLocation, float xpos, float ypos, float veloX, float veloY) {
        super(imageLocation, xpos, ypos, 0f, 1f); // rotation = 0f, size = 1f (can adjust if needed)
        this.position.z = Constants.PROJECTILE_ZLEVEL; // Set Z-level for projectile rendering
        this.velocity = new Vector2(veloX, veloY);

        GameFactory.projectileManager.addProjectile(this);
    }

    public void update() {
        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;
        this.lifeTime += 0.0001f;
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        TextureRegion region = GameFactory.textureAtlas.getRegion(this.textureResource);

        batchRenderer.addTexture(region, this.position.x, this.position.y, this.position.z,
                new TileParameters(null, velocity.x, velocity.y, 1, 1, null, LightSystem.getLightSources()));
    }
}
