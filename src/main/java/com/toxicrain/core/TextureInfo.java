package com.toxicrain.core;

public class TextureInfo {
    public final int textureId;
    public final int width;
    public final int height;

    public TextureInfo(int textureId, int width, int height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }
    public int getTextureId() {
        return textureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}