package com.toxicrain.util;

import com.toxicrain.core.Color;
import com.toxicrain.core.Logger;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.texture.TextureSystem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * The TextEngine class provides a way to render "text" on the screen
 *
 * @author Gabefry, strubium
 */
public class TextEngine {
    private static final float SCALE_FACTOR = 2.0f;
    private static final float TEXT_SCALE = 1.2f; // Scale factor for text rendering
    private static Font font;
    private final float transparency;

    // Cache for text and textures
    private Map<String, TextureInfo> textureCache;

    public TextEngine(Font font, float transparency) {
        this.font = font;
        this.transparency = transparency;
        this.textureCache = new HashMap<>();  // Initialize the cache
    }

    public void render(BatchRenderer batchRenderer, String toWrite, int xOffset, int yOffset) {
        float scale = GameFactory.player.cameraZ / 30;
        float baseX = GameFactory.player.cameraX - (toWrite.length() * scale) / SCALE_FACTOR;
        float baseY = GameFactory.player.cameraY - yOffset * scale;

        // Check if the text is in the cache
        TextureInfo textureInfo = textureCache.get(toWrite.trim());
        if (textureInfo == null) {
            // Create a BufferedImage to render the new text
            BufferedImage textImage = createTextImage(toWrite);

            // Convert the BufferedImage to a TextureInfo
            textureInfo = convertToTextureInfo(textImage);

            // Cache the texture for this text
            textureCache.put(toWrite.trim(), textureInfo);
        }

        // Render the texture using BatchRenderer
        batchRenderer.addTexture(
                textureInfo,
                baseX + xOffset * SCALE_FACTOR * scale,  // Adjust for camera and scaling
                baseY,
                TEXT_SCALE,
                0,  // Rotation (assuming 0 for no rotation)
                scale,
                scale,
                com.toxicrain.core.Color.toFloatArray(transparency, Color.WHITE)  // Applying color/transparency
        );
    }

    private BufferedImage createTextImage(String text) {
        // Create a temporary image to measure text size
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();

        // Calculate width and height of the text
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        // Create the final image with the calculated width and height
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.drawString(text, 0, metrics.getAscent()); // Draw the text
        g2d.dispose();

        return flipImageVertically(image);
    }

    private TextureInfo convertToTextureInfo(BufferedImage image) {
        // Save BufferedImage to a temporary file
        try {
            File tempFile = File.createTempFile("textTexture", ".png");
            ImageIO.write(image, "png", tempFile);

            // Load the texture using your existing loadTexture method
            TextureInfo textureInfo = TextureSystem.loadTexture(tempFile.getAbsolutePath());

            // Optionally delete the temporary file
            tempFile.deleteOnExit(); // Mark for deletion when the JVM exits

            return textureInfo;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert BufferedImage to TextureInfo", e);
        }
    }

    private BufferedImage flipImageVertically(BufferedImage image) {
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), 0, image.getHeight(), image.getWidth(), 0, null);
        g.dispose();
        return flipped;
    }

    public static float getTextWidth(String text) {
        // Create a temporary image to measure text size
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);  // Set the font for measuring

        // Get FontMetrics after setting the font
        FontMetrics metrics = g2d.getFontMetrics();

        // Calculate the width of the text
        float textWidth = metrics.stringWidth(text);

        // Clean up
        g2d.dispose();  // Dispose of graphics context to free resources

        return textWidth;  // Return the width of the text in pixels
    }
}
