package com.toxicrain.core;

import com.toxicrain.util.TextureUtils;

/**
 * The TextureInfo class provides information about the given texture
 * Textures are created in {@link TextureUtils}
 *
 * @author strubium
 */
public class TextureInfo {
    public final int textureId;
    public final int width;
    public final int height;
    
    /**
     * Create a new TextureInfo
     * @param textureId the id of the texture, used by OpenGL from rendering
     * @param width the width of the texture
     * @param height the height of the texture
     */
    public TextureInfo(int textureId, int width, int height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Texture ID: " + textureId + " Width: "+ width +" Height: " + height;
    }
}