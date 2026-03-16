package com.toxicrain.rainengine.artifacts;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.datatypes.Color;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.eventbus.events.ArtifactUpdateEvent;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.texture.TextureSystem;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

/**
 * A base class for all artifacts that can be rendered in the world.
 * <p>
 * Handles texture binding, positioning, rotation, and basic rendering logic.
 * Artifacts that extend this class automatically gain simple rendering
 * capabilities and world positioning.
 */
@Getter @Setter
public abstract class RenderableArtifact implements IArtifact {

    protected Vector3f position;
    protected float rotation;

    protected Resource textureResource;
    protected TextureRegion textureRegion;

    public RenderableArtifact(Resource textureResource, float x, float y, float rotation) {
        this.textureResource = textureResource;
        this.position = new Vector3f(x, y, 1);
        this.rotation = rotation;
    }
    public RenderableArtifact(Resource textureResource, Vector3f position,  float rotation) {
        this.textureResource = textureResource;
        this.position = position;
        this.rotation = rotation;
    }

    @Override
    public void update(double deltaTime){
        textureRegion = TextureSystem.getInstance().getRegion(this.textureResource);

        SmeagleBus.getInstance().post(new ArtifactUpdateEvent(this));
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        batchRenderer.addTexture(
                textureRegion,
                position.x, position.y, position.z,
                new TileParameters(rotation, textureRegion.getU0(), textureRegion.getV0(), 1f, 1f, Color.toFloatArray(Color.WHITE), null)
        );
    }

}
