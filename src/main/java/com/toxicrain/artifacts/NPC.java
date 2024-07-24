package com.toxicrain.artifacts;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

public class NPC {
    private float x, y;
    private float speed;
    private TextureInfo texture;

    public NPC(float x, float y, float speed, TextureInfo texture) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.texture = texture;
        if (this.texture == null) {
            System.out.println("Texture is null in NPC constructor for position (" + x + ", " + y + ")");
        }
    }

    public void update() {
        if(Player.cameraX > x){
            x += 0.005f;
        }
        if(Player.cameraX < x){
            x -= 0.005f;
        }
        if(Player.cameraY > y){
            y += 0.005f;
        }
        if(Player.cameraY < y){
            y -= 0.005f;
        }
    }

    public void render(BatchRenderer batchRenderer) {
        if (texture != null) {
            batchRenderer.addTexturePos(texture, x, y, 1.1f, Player.cameraX, Player.cameraY, Color.toFloatArray(Color.RED));
        } else {
            System.out.println("Texture is null for NPC at position (" + x + ", " + y + ")");
        }
    }

    public TextureInfo getTexture() {
        return texture;
    }

    public void setTexture(TextureInfo texture) {
        this.texture = texture;
    }
}
