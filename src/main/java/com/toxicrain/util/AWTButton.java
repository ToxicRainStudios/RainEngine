package com.toxicrain.util;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
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

    // Define a static font instance for reuse
    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Color BUTTON_COLOR = Color.LIGHT_GRAY;
    private static final Color BORDER_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = Color.BLACK;

    public AWTButton(float centerX, float centerY, float width, float height, String label) {
        // Adjusting the x and y positions to center the button
        this.x = centerX - (width / 2); // Set X position centered
        this.y = centerY - (height / 2); // Set Y position centered
        this.width = width; // Set button width
        this.height = height; // Set button height
        this.label = label; // Set button label text
        createButtonImage(); // Create the visual representation of the button
    }

    public void renderInteractionZone(Graphics2D g2d) {
        float topLeftX = x - (width / 2); // Calculate top-left X position
        float topLeftY = y - (height / 2); // Calculate top-left Y position

        // Set color and stroke for the interaction zone outline
        g2d.setColor(Color.RED); // Set color to red for visibility
        g2d.setStroke(new BasicStroke(2)); // Thicker line for better visibility

        // Draw the interaction zone as a rectangle
        g2d.drawRect((int) topLeftX, (int) topLeftY, (int) width, (int) height);
    }



    private void createButtonImage() {
        buttonImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buttonImage.createGraphics();

        drawButtonBackground(g2d);
        drawButtonBorder(g2d);
        drawLabelText(g2d);

        g2d.dispose(); // Dispose the graphics context

        // Convert BufferedImage to TextureInfo for rendering
        textureInfo = convertToTextureInfo(flipImageVertically(buttonImage));
    }

    private void drawButtonBackground(Graphics2D g2d) {
        g2d.setColor(BUTTON_COLOR);
        g2d.fillRect(0, 0, (int) width, (int) height); // Fill the button area
    }

    private void drawButtonBorder(Graphics2D g2d) {
        g2d.setColor(BORDER_COLOR);
        g2d.drawRect(0, 0, (int) width - 1, (int) height - 1); // Draw a border
    }

    private void drawLabelText(Graphics2D g2d) {
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(BUTTON_FONT);
        FontMetrics metrics = g2d.getFontMetrics(); // Get font metrics for centering
        int labelX = (int) (width - metrics.stringWidth(label)) / 2; // Calculate X position for centered text
        int labelY = (int) (height - metrics.getHeight()) / 2 + metrics.getAscent(); // Calculate Y position for centered text
        g2d.drawString(label, labelX, labelY); // Draw the label text
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
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -image.getHeight());
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flipped.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, transform, null);
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
        // Check if the mouse coordinates are over the button
        float adjustedX = x + (width / 2);
        float adjustedY = y + (height / 2);

        return mouseX >= adjustedX && mouseX <= (adjustedX + width) &&
                mouseY >= adjustedY && mouseY <= (adjustedY + height);
    }

    public void onClick() {
        // Action performed when the button is clicked
        System.out.println(label + " clicked!"); // Debug print to console
    }
}
