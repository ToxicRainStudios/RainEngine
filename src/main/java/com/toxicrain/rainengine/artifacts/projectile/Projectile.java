package com.toxicrain.rainengine.artifacts.projectile;

import com.toxicrain.rainengine.artifacts.IArtifact;
import com.toxicrain.rainengine.artifacts.RenderableArtifact;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.registries.manager.ArtifactManager;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.light.LightSystem;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
public class Projectile extends RenderableArtifact implements IArtifact {

    private final Vector2f velocity;
    private float lifeTime;

    public Projectile(Resource imageLocation, float xpos, float ypos, float veloX, float veloY) {
        super(imageLocation, xpos, ypos, 0f); // rotation = 0f
        this.position.z = Constants.PROJECTILE_ZLEVEL; // Set Z-level for projectile rendering
        this.velocity = new Vector2f(veloX, veloY);

        ArtifactManager.getInstance().addArtifact(this);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        this.position.x += this.velocity.x;
        this.position.y += this.velocity.y;
        this.lifeTime += 0.0001f;
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(this.textureRegion, this.position.x, this.position.y, this.position.z,
                new TileParameters(null, velocity.x, velocity.y, 1, 1, null, LightSystem.getLightSources()));
    }
}
