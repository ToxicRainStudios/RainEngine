package com.toxicrain.util;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AWTButton {
    private BufferedImage buttonImage;
    public float x;       // X-coordinate (horizontal position from center)
    public float y;       // Y-coordinate (vertical position from center)
    public float width;   // Width of the button
    public float height;  // Height of the button
    private String label; // Text label displayed on the button
    private TextureInfo textureInfo; // Texture info for rendering the button

    public AWTButton(float centerX, float centerY, float width, float height, String label) {
        // Adjusting the x and y positions to center the button
        this.x = centerX - (width / 2); // Set X position centered
        this.y = centerY - (height / 2); // Set Y position centered
        this.width = width; // Set button width
        this.height = height; // Set button height
        this.label = label; // Set button label text
        createButtonImage(); // Create the visual representation of the button
    }

    private void createButtonImage() {
        buttonImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buttonImage.createGraphics();

        // Draw button background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, (int) width, (int) height); // Fill the button area

        // Draw button border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, (int) width - 1, (int) height - 1); // Draw a border

        // Draw the label text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics metrics = g2d.getFontMetrics(); // Get font metrics for centering
        int labelX = (int) (width - metrics.stringWidth(label)) / 2; // Calculate X position for centered text
        int labelY = (int) (height - metrics.getHeight()) / 2 + metrics.getAscent(); // Calculate Y position for centered text
        g2d.drawString(label, labelX, labelY); // Draw the label text

        g2d.dispose(); // Dispose the graphics context

        // Convert BufferedImage to TextureInfo for rendering
        textureInfo = convertToTextureInfo(flipImageVertically(buttonImage));
    }

    private TextureInfo convertToTextureInfo(BufferedImage image) {
        try {
            File tempFile = File.createTempFile("buttonTexture", ".png");
            ImageIO.write(image, "png", tempFile); // Save the image to a temp file
            TextureInfo textureInfo = TextureSystem.loadTexture(tempFile.getAbsolutePath());
            tempFile.deleteOnExit(); // Clean up temp file on exit
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
        return flipped; // Flip the image for correct rendering
    }

    public void render(BatchRenderer batchRenderer) {
        // Adjust rendering position based on width and height
        float adjustedX = x + (width / 2);
        float adjustedY = y + (height / 2);

        // Render the button using the batch renderer
        batchRenderer.addTexture(textureInfo, adjustedX, adjustedY, 1.0f, 0, 1.0f, 1.0f, new float[]{1, 1, 1, 1});
    }

    public boolean isMouseOver(float mouseX, float mouseY) {
        // Check if the centered mouse coordinates are over the button
        return mouseX >= (x - width / 2) && mouseX <= (x + width / 2) &&
                mouseY >= (y - height / 2) && mouseY <= (y + height / 2);
    }


    public void onClick() {
        // Action performed when the button is clicked
        System.out.println(label + " clicked!"); // Debug print to console
    }

    public static float[] convertMousePosition(float mouseX, float mouseY, float screenWidth, float screenHeight) {
        float centeredX = mouseX - (screenWidth / 2);
        float centeredY = mouseY - (screenHeight / 2);
        return new float[]{centeredX, centeredY};
    }
}
