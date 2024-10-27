package com.toxicrain.util;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.texture.TextureInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * The TextEngine class provides a way to render "text" on the screen
 */
public class TextEngine {
    private static final float TEXT_SCALE = 1.2f;
    private final Font font;
    private final float transparency;
    private final Map<String, TextureInfo> textureCache;

    public TextEngine(Font font, float transparency) {
        this.font = font;
        this.transparency = transparency;
        this.textureCache = new HashMap<>();
    }

    public void render(BatchRenderer batchRenderer, String toWrite, int xOffset, int yOffset) {
        TextureInfo textureInfo = textureCache.computeIfAbsent(toWrite.trim(), text -> {
            BufferedImage textImage = createTextImage(text);
            return GraphicsUtil.convertToTextureInfo(textImage);
        });

        batchRenderer.addTexture(
                textureInfo,
                xOffset,
                yOffset,
                TEXT_SCALE,
                0,
                1.0f,
                1.0f,
                new float[]{1, 1, 1, transparency}
        );
    }

    private BufferedImage createTextImage(String text) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.drawString(text, 0, metrics.getAscent());
        g2d.dispose();
        return GraphicsUtil.flipImageVertically(image);
    }
}
