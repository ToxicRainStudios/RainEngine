package com.toxicrain.texture;

/**
 * The TextureInfo class provides information about the given texture
 * Textures are created in {@link TextureSystem}
 *
 * @author strubium
 */
public class TextureInfo {
    public final int textureId;
    public final int width;
    public final int height;
    public final boolean isTransparent;


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
        this.isTransparent = false;
    }

    /**
     * Create a new TextureInfo
     * @param textureId the id of the texture, used by OpenGL from rendering
     * @param width the width of the texture
     * @param height the height of the texture
     */
    public TextureInfo(int textureId, int width, int height, boolean isTransparent) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.isTransparent = isTransparent;
    }

    @Override
    public String toString() {
        return "Texture ID: " + textureId + " Width: "+ width +" Height: " + height;
    }
}