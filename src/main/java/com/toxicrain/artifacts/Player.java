package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;

public class Player{

    public float posX;
    public float posY;
    public float posZ;
    public TextureInfo texture;

    public Player(float posX, float posY, float posZ, TextureInfo texture) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
    }
}