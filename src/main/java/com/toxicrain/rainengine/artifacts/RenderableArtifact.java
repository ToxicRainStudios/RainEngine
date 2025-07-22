package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Color;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.factories.GameFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * A base class for all artifacts that can be rendered in the world.
 * <p>
 * Handles texture binding, positioning, rotation, and basic rendering logic.
 * Artifacts that extend this class automatically gain simple rendering
 * capabilities and world positioning.
 */
@Getter @Setter
public abstract class RenderableArtifact {

    protected TilePos position;
    protected float rotation;
    protected float size;

    protected Resource textureResource;

    public RenderableArtifact(Resource textureResource, float x, float y, float rotation, float size) {
        this.textureResource = textureResource;
        this.position = new TilePos(x, y, 1);
        this.rotation = rotation;
        this.size = size;
    }

    public void render(BatchRenderer batchRenderer) {
        TextureRegion region = GameFactory.textureAtlas.getRegion(this.textureResource);

        batchRenderer.addTexture(
                region,
                position.x, position.y, Constants.NPC_ZLEVEL,
                new TileParameters(rotation, region.getU0(), region.getV0(), 1f, 1f, Color.toFloatArray(Color.WHITE), null)
        );
    }

}
