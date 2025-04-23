package com.toxicrain.rainengine.texture;

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
     * @param isTransparent If the texture has transparency
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TextureInfo that = (TextureInfo) obj;
        return textureId == that.textureId && width == that.width && height == that.height && isTransparent == that.isTransparent;
    }
}