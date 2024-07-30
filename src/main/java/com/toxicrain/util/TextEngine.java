package com.toxicrain.util;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;


import java.util.HashMap;
import java.util.Map;

/**
 * The TextEngine class provides a way to render "text" on the screen
 *
 * @author Gabefry (the darkened sigma)
 */
public class TextEngine {
    private static final float SCALE_FACTOR = 2.0f;
    private static final float TEXT_SCALE = 1.2f; // Scale factor for text rendering
    private static final Map<Character, TextureInfo> textureMap = new HashMap<>();

    static {
        // Initialize the texture map
        textureMap.put('a', TextureUtils.letterA);
        textureMap.put('b', TextureUtils.letterB);
        textureMap.put('c', TextureUtils.letterC);
        textureMap.put('d', TextureUtils.letterD);
        textureMap.put('e', TextureUtils.letterE);
        textureMap.put('f', TextureUtils.letterF);
        textureMap.put('g', TextureUtils.letterG);
        textureMap.put('h', TextureUtils.letterH);
        textureMap.put('i', TextureUtils.letterI);
        textureMap.put('j', TextureUtils.letterJ);
        textureMap.put('k', TextureUtils.letterK);
        textureMap.put('l', TextureUtils.letterL);
        textureMap.put('m', TextureUtils.letterM);
        textureMap.put('n', TextureUtils.letterN);
        textureMap.put('o', TextureUtils.letterO);
        textureMap.put('p', TextureUtils.letterP);
        textureMap.put('q', TextureUtils.letterQ);
        textureMap.put('r', TextureUtils.letterR);
        textureMap.put('s', TextureUtils.letterS);
        textureMap.put('t', TextureUtils.letterT);
        textureMap.put('u', TextureUtils.letterU);
        textureMap.put('v', TextureUtils.letterV);
        textureMap.put('w', TextureUtils.letterW);
        textureMap.put('x', TextureUtils.letterX);
        textureMap.put('y', TextureUtils.letterY);
        textureMap.put('z', TextureUtils.letterZ);
        textureMap.put(' ', TextureUtils.letterSPACE);
    }

    private String toWrite;
    private int xOffset;
    private int yOffset;

    public TextEngine(String input, int x, int y) {
        this.toWrite = input;
        this.xOffset = x;
        this.yOffset = y;
    }

    public void render(BatchRenderer batchRenderer) {
        float scale = Player.cameraZ / 30;
        float baseX = Player.cameraX - (toWrite.length() * scale) / SCALE_FACTOR;
        float baseY = Player.cameraY - yOffset * scale;

        for (int i = toWrite.length() - 1; i >= 0; i--) {
            char letter = toWrite.charAt(i);
            TextureInfo texture = textureMap.getOrDefault(Character.toLowerCase(letter), TextureUtils.missingTexture);
            float x = baseX + (i + xOffset) * SCALE_FACTOR * scale;
            batchRenderer.addTexture(texture, x, baseY, TEXT_SCALE, 0, scale, scale, Color.toFloatArray(1.0f, Color.WHITE));
        }
    }
}
