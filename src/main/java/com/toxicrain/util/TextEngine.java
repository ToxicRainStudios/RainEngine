package com.toxicrain.util;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;


import static com.toxicrain.core.GameEngine.window;

/**
 * The TextEngine class provides a way to render "text" on the screen
 *
 * @author Gabefry (the darkened sigma)
 */
public class TextEngine {
    String toWrite = "hello";
    int yOffset;
    int xOffset;
    public TextEngine(String input, int x, int y) {
        toWrite = input;
        xOffset = x;
        yOffset = y;


    }

    public void render(BatchRenderer batchRenderer) {
        float scale = Player.cameraZ / 30;
        for (int i = toWrite.length() - 1; i >= 0; i--) {

            batchRenderer.addTexture(letterToTexture(toWrite.charAt(i)), Player.cameraX + (i + xOffset) * 2 * scale - ((float) toWrite.length() * scale), Player.cameraY - yOffset * scale, 1.2f, 0, scale, scale, Color.toFloatArray(1.0f, Color.WHITE));

        }


    }

    public static TextureInfo letterToTexture(char toProcess){
        switch (Character.toLowerCase(toProcess)){
            case ('a'):
                return TextureUtils.letterA;
            case ('b'):
                return TextureUtils.letterB;
            case ('c'):
                return TextureUtils.letterC;
            case ('d'):
                return TextureUtils.letterD;
            case ('e'):
                return TextureUtils.letterE;
            case ('f'):
                return TextureUtils.letterF;
            case ('g'):
                return TextureUtils.letterG;
            case ('h'):
                return TextureUtils.letterH;
            case ('i'):
                return TextureUtils.letterI;
            case ('j'):
                return TextureUtils.letterJ;
            case ('k'):
                return TextureUtils.letterK;
            case ('l'):
                return TextureUtils.letterL;
            case ('m'):
                return TextureUtils.letterM;
            case ('n'):
                return TextureUtils.letterN;
            case ('o'):
                return TextureUtils.letterO;
            case ('p'):
                return TextureUtils.letterP;
            case ('q'):
                return TextureUtils.letterQ;
            case ('r'):
                return TextureUtils.letterR;
            case ('s'):
                return TextureUtils.letterS;
            case ('t'):
                return TextureUtils.letterT;
            case ('u'):
                return TextureUtils.letterU;
            case ('v'):
                return TextureUtils.letterV;
            case ('w'):
                return TextureUtils.letterW;
            case ('x'):
                return TextureUtils.letterX;
            case ('y'):
                return TextureUtils.letterY;
            case ('z'):
                return TextureUtils.letterZ;
            case (' '):
                return TextureUtils.letterSPACE;
            default:
                return TextureUtils.missingTexture;
        }

    }

}
