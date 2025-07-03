package com.toxicrain.rainengine.core.datatypes;

import com.toxicrain.rainengine.texture.TextureInfo;
import lombok.Getter;

@Getter
public class TileInfo {

    private final Resource textureResource;
    private final TextureInfo texture;
    private final boolean collision;

    public TileInfo(Resource textureResource, TextureInfo texture, boolean collision) {
        this.textureResource = textureResource;
        this.texture = texture;
        this.collision = collision;
    }

    public String getTextureName() {
        return textureResource.toString();
    }
}
