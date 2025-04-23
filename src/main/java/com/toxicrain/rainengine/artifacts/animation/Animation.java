package com.toxicrain.rainengine.artifacts.animation;

import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureSystem;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Animation {
    private BufferedImage[] frames; // Array of frames for the animation
    private TextureInfo[] frameTextures; // TextureInfo for each frame
    private int currentFrame; // Index of the current frame being displayed
    private long lastFrameTime; // Time when the last frame change happened
    private final int frameDuration; // Duration of each frame in milliseconds
    private final boolean looping; // Whether the animation should loop

    @Getter
    private boolean finished; // Whether the animation has finished

    private float x; // X-coordinate (horizontal position)
    private float y; // Y-coordinate (vertical position)
    private float width; // Width of the animation
    private float height; // Height of the animation

    private float scaleX; // Scale factor for width
    private float scaleY; // Scale factor for height

    public Animation(String spriteSheetPath, int frameWidth, int frameHeight, int frameCount, int frameDuration, boolean looping) {
        this.frameDuration = frameDuration; // Set duration per frame
        this.looping = looping; // Set if animation should loop
        this.currentFrame = 0; // Initialize to first frame
        this.finished = false; // Initially not finished
        this.scaleX = 1.0f; // Default scale factor for width
        this.scaleY = 1.0f; // Default scale factor for height

        loadFrames(spriteSheetPath, frameWidth, frameHeight, frameCount); // Load the frames from a sprite sheet
        convertFramesToTextures(); // Convert the frames to textures
    }

    /**
     * Loads the frames from a sprite sheet.
     *
     * @param spriteSheetPath Path to the sprite sheet image.
     * @param frameWidth      Width of each frame in the sprite sheet.
     * @param frameHeight     Height of each frame in the sprite sheet.
     * @param frameCount      Number of frames in the sprite sheet.
     */
    private void loadFrames(String spriteSheetPath, int frameWidth, int frameHeight, int frameCount) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(spriteSheetPath));
            frames = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = spriteSheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load animation frames from sprite sheet", e);
        }
    }

    /**
     * Converts the BufferedImage frames into TextureInfo for rendering.
     */
    private void convertFramesToTextures() {
        frameTextures = new TextureInfo[frames.length];
        for (int i = 0; i < frames.length; i++) {
            frameTextures[i] = convertToTextureInfo(flipImageVertically(frames[i]));
        }
    }

    /**
     * Updates the animation state by advancing the frame if enough time has passed.
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            lastFrameTime = currentTime;
            currentFrame++;
            if (currentFrame >= frames.length) {
                if (looping) {
                    currentFrame = 0; // Reset to first frame if looping
                } else {
                    currentFrame = frames.length - 1; // Stop at the last frame
                    finished = true; // Mark animation as finished
                }
            }
        }
    }

    /**
     * Renders the current frame of the animation.
     */
    public void render(BatchRenderer batchRenderer) {
        if (!finished) {
            // Apply scaling factors to width and height
            float scaledWidth = width * scaleX;
            float scaledHeight = height * scaleY;
            batchRenderer.addTexture(frameTextures[currentFrame], x, y, 1.0f, 0, scaledWidth, scaledHeight, new float[]{1, 1, 1, 1});
        }
    }

    /**
     * Resets the animation to its first frame.
     */
    public void reset() {
        currentFrame = 0;
        finished = false;
        lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Flips a BufferedImage vertically for correct rendering.
     */
    private BufferedImage flipImageVertically(BufferedImage image) {
        AffineTransform transform = AffineTransform.getScaleInstance(1, -1);
        transform.translate(0, -image.getHeight());
        BufferedImage flipped = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        flipped.createGraphics().drawImage(image, transform, null);
        return flipped;
    }

    /**
     * Converts a BufferedImage to TextureInfo for rendering.
     */
    private TextureInfo convertToTextureInfo(BufferedImage image) {
        try {
            File tempFile = File.createTempFile("frameTexture", ".png");
            ImageIO.write(image, "png", tempFile);
            TextureInfo textureInfo = TextureSystem.loadTexture(tempFile.getAbsolutePath());
            tempFile.deleteOnExit();
            return textureInfo;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert frame to TextureInfo", e);
        }
    }

    /**
     * Sets the position where the animation will be rendered.
     *
     * @param x X-coordinate (horizontal position).
     * @param y Y-coordinate (vertical position).
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the size of the animation (scales width and height).
     *
     * @param width  Width of the animation.
     * @param height Height of the animation.
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the scale factors for the animation.
     *
     * @param scaleX Scale factor for width.
     * @param scaleY Scale factor for height.
     */
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
