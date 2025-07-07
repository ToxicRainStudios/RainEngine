package com.toxicrain.rainengine.core.datatypes;

import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.texture.TextureSystem;
import lombok.Getter;

@Getter
public class TileInfo {

    private final Resource textureResource;
    private final TextureRegion textureRegion;
    private final boolean collision;

    public TileInfo(Resource textureResource, boolean collision) {
        this.textureResource = textureResource;
        this.textureRegion = TextureSystem.getRegion(textureResource);
        this.collision = collision;
    }

    public String getTextureName() {
        return textureResource.toString();
    }
}
