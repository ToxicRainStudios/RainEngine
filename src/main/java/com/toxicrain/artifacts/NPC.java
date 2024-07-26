package com.toxicrain.artifacts;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Color;

import static com.toxicrain.util.TextureUtils.playerTexture;

public class NPC {
float X;
float Y;
float directionX;
float directionY;
int aiType = 0;


    public  NPC(float XPOS, float YPOS, float rotation, int ai) {
        directionX = (float) Math.cos(rotation);
        directionY = (float) Math.sin(rotation);
        aiType = ai;
    }
    public void runAI(NPC character){
        switch (character.aiType){
            case(1):
                character.X = Player.posX - 3;
                character.Y = Player.posY - 1;


            case(2):
                character.directionX = Player.posX;
                character.directionY = Player.posY;
                character.X = character.X + (Player.posX-character.X) * 0.0005f;
                character.Y = character.Y + (Player.posY-character.Y) * 0.0005f;
        }


    }



    public static void render(BatchRenderer batchRenderer, NPC character){



        batchRenderer.addTexturePos(playerTexture, character.X, character.Y, 1.01f, character.directionX,character.directionY , 1,1, Color.toFloatArray(1.0f, Color.WHITE));
    }
}

