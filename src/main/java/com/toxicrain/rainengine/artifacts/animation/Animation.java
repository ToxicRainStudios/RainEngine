package com.toxicrain.rainengine.artifacts.animation;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.texture.TextureAtlas;
import com.toxicrain.rainengine.texture.TextureRegion;
import lombok.Getter;

public class Animation {

    private TextureRegion[] frames; // Array of texture regions for the animation
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

    public Animation(String frameNamePrefix, int frameCount, int frameDuration, boolean looping) {
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.currentFrame = 0;
        this.finished = false;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;

        loadFramesFromAtlas(frameNamePrefix, frameCount);
    }

    /**
     * Loads the frames directly from the texture atlas.
     * Assumes frame names are like "animation_walk_0", "animation_walk_1", etc.
     */
    private void loadFramesFromAtlas(String frameNamePrefix, int frameCount) {
        TextureAtlas atlas = GameFactory.textureAtlas;
        frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            String frameName = frameNamePrefix + "_" + i;
            TextureRegion region = atlas.getRegion(new Resource(frameName));
            if (region == null) {
                throw new RuntimeException("Frame not found in texture atlas: " + frameName);
            }
            frames[i] = region;
        }
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            lastFrameTime = currentTime;
            currentFrame++;
            if (currentFrame >= frames.length) {
                if (looping) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
        }
    }

    public void render(BatchRenderer batchRenderer) {
        if (!finished) {
            float scaledWidth = width * scaleX;
            float scaledHeight = height * scaleY;

            TextureRegion region = frames[currentFrame];

            batchRenderer.addTexture(region, x, y, 1.0f,
                    new TileParameters(region.getU0(), region.getV0(), 0f, scaledWidth, scaledHeight, new float[]{1, 1, 1, 1}, null));
        }
    }

    public void reset() {
        currentFrame = 0;
        finished = false;
        lastFrameTime = System.currentTimeMillis();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
