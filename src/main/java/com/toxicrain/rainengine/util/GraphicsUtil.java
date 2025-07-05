package com.toxicrain.rainengine.util;

import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphicsUtil {

    // Flips a BufferedImage vertically
    public static BufferedImage flipImageVertically(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, image.getHeight(), image.getWidth(), 0, null);
        g.dispose();
        return flipped;
    }

    // Converts BufferedImage to TextureInfo for rendering
//    public static TextureInfo convertToTextureInfo(BufferedImage image) {
//        try {
//            File tempFile = File.createTempFile("texture", ".png");
//            ImageIO.write(image, "png", tempFile);
//            TextureInfo textureInfo = TextureSystem.loadTexture(tempFile.getAbsolutePath());
//            tempFile.deleteOnExit(); // Clean up temp file on exit
//            return textureInfo;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to convert BufferedImage to TextureInfo", e);
//        }
//    }

    // Measures the width of a text with a given font
    public static float getTextWidth(String text, Font font) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        float textWidth = metrics.stringWidth(text);
        g2d.dispose();
        return textWidth;
    }

    // Measures the height of a text with a given font
    public static float getTextHeight(Font font) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        float textHeight = metrics.getHeight();
        g2d.dispose();
        return textHeight;
    }
}

