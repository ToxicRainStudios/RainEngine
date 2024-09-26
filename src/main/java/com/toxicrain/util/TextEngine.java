package com.toxicrain.util;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.render.BatchRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The TextEngine class provides a way to render "text" on the screen
 *
 * @author Gabefry, strubium
 */
public class TextEngine {
    private static final float SCALE_FACTOR = 2.0f;
    private static final float TEXT_SCALE = 1.2f; // Scale factor for text rendering
    private final Font font;
    private final float transparency;

    public TextEngine(Font font, float transparency) {
        this.font = font;
        this.transparency = transparency;
    }

    public void render(BatchRenderer batchRenderer, String toWrite, int xOffset, int yOffset) {
        float scale = Player.cameraZ / 30;
        float baseX = Player.cameraX - (toWrite.length() * scale) / SCALE_FACTOR;
        float baseY = Player.cameraY - yOffset * scale;

        // Create a BufferedImage to render the text
        BufferedImage textImage = createTextImage(toWrite);

        // Convert the BufferedImage to a TextureInfo
        TextureInfo textureInfo = convertToTextureInfo(textImage);

        // Render the texture using BatchRenderer
        batchRenderer.addTexture(
                textureInfo,
                baseX + xOffset * SCALE_FACTOR * scale,
                baseY,
                TEXT_SCALE,
                0,
                scale,
                scale,
                Color.toFloatArray(transparency, Color.WHITE)
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
            TextureInfo textureInfo = TextureUtils.loadTexture(tempFile.getAbsolutePath());

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
}
