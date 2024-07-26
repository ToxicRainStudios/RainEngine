package com.toxicrain.artifacts;

import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

import static com.toxicrain.util.TextureUtils.playerTexture;


public class Enemy implements IArtifact {

    public float speed;
    public float x;
    public float y;
    public float z;
    public float rotX;
    public float rotY;



    public Enemy(float speed, float cordX, float cordY, float cordZ){
        this.speed = speed;
        x = cordX;
        y = cordY;
        z = cordZ;
        rotX = Player.cameraX;
        rotY = Player.cameraY;
    }

    public void update() {
        float distanceX = Player.cameraX - x;
        float distanceY = Player.cameraY - y;
        float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance > speed) {
            x += distanceX / distance * speed;
            y += distanceY / distance * speed;
        } else {
            x = Player.cameraX;
            y = Player.cameraY;
        }

        // Update rotation to face the player
        rotX = Player.cameraX;
        rotY = Player.cameraY;
    }


    public void render(BatchRenderer batchRenderer){
        batchRenderer.addTexturePos(playerTexture, x, y, z, rotX, rotY, Color.toFloatArray(Color.RED));

    }
}