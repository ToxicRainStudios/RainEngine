package com.toxicrain.rainengine.texture;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {

    private final Map<Resource, TextureRegion> regionMap = new HashMap<>();
    private final int atlasTextureId;
    private final int atlasSize;

    private ByteBuffer atlasBuffer; // Store the atlas buffer for saving

    public TextureAtlas(int atlasSize) {
        this.atlasSize = atlasSize;
        this.atlasTextureId = glGenTextures();
    }

    public void buildAtlas(String directory) {
        try {
            List<Path> imagePaths = Files.walk(Paths.get(directory))
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .toList();

            int imageCount = imagePaths.size();
            int gridSize = (int) Math.ceil(Math.sqrt(imageCount)); // Simple grid
            int cellSize = atlasSize / gridSize;

            atlasBuffer = BufferUtils.createByteBuffer(atlasSize * atlasSize * 4);

            int index = 0;

            for (Path path : imagePaths) {
                String filePath = path.toString();
                Resource resource = Resource.fromFile(directory, path);

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer widthBuffer = stack.mallocInt(1);
                    IntBuffer heightBuffer = stack.mallocInt(1);
                    IntBuffer channelsBuffer = stack.mallocInt(1);

                    ByteBuffer image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
                    if (image == null) {
                        throw new RuntimeException("Failed to load texture: " + filePath + " - " + stbi_failure_reason());
                    }

                    int imageWidth = widthBuffer.get();
                    int imageHeight = heightBuffer.get();

                    int row = index / gridSize;
                    int col = index % gridSize;

                    int xOffset = col * cellSize;
                    int yOffset = row * cellSize;

                    copyImageToAtlas(atlasBuffer, atlasSize, image, imageWidth, imageHeight, xOffset, yOffset);

                    float u0 = (float) xOffset / atlasSize;
                    float v0 = (float) yOffset / atlasSize;
                    float u1 = (float) (xOffset + imageWidth) / atlasSize;
                    float v1 = (float) (yOffset + imageHeight) / atlasSize;

                    TextureInfo textureInfo = new TextureInfo(atlasTextureId, atlasSize, atlasSize, checkTransparency(image, imageWidth, imageHeight));
                    TextureRegion region = new TextureRegion(textureInfo, u0, v0, u1, v1);
                    regionMap.put(resource, region);

                    stbi_image_free(image);
                    index++;
                }
            }

            uploadAtlasToGPU(atlasBuffer);

        } catch (IOException e) {
            throw new RuntimeException("Failed to build texture atlas.", e);
        }
    }

    private void copyImageToAtlas(ByteBuffer atlasBuffer, int atlasSize, ByteBuffer image, int imageWidth, int imageHeight, int xOffset, int yOffset) {
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int srcIndex = (y * imageWidth + x) * 4;
                int dstIndex = ((yOffset + y) * atlasSize + (xOffset + x)) * 4;

                atlasBuffer.put(dstIndex, image.get(srcIndex));         // R
                atlasBuffer.put(dstIndex + 1, image.get(srcIndex + 1)); // G
                atlasBuffer.put(dstIndex + 2, image.get(srcIndex + 2)); // B
                atlasBuffer.put(dstIndex + 3, image.get(srcIndex + 3)); // A
            }
        }
    }

    private ByteBuffer originalAtlasBuffer;


    private void uploadAtlasToGPU(ByteBuffer atlasBuffer) {
        // Store a copy for saving
        this.originalAtlasBuffer = BufferUtils.createByteBuffer(atlasBuffer.capacity());
        for (int i = 0; i < atlasBuffer.capacity(); i++) {
            this.originalAtlasBuffer.put(i, atlasBuffer.get(i));
        }

        glBindTexture(GL_TEXTURE_2D, atlasTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, atlasSize, atlasSize, 0, GL_RGBA, GL_UNSIGNED_BYTE, atlasBuffer);
        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        RainLogger.RAIN_LOGGER.info("Texture atlas uploaded to GPU.");
    }


    private boolean checkTransparency(ByteBuffer image, int width, int height) {
        int pixelCount = width * height;
        for (int i = 0; i < pixelCount; i++) {
            int alpha = image.get(i * 4 + 3) & 0xFF;
            if (alpha < 255) return true;
        }
        return false;
    }

    public TextureRegion getRegion(Resource resource) {
        return regionMap.get(resource);
    }

    public int getAtlasTextureId() {
        return atlasTextureId;
    }

    /**
     * Saves the current texture atlas to an image file.
     *
     * @param filePath the path to save the image.
     */
    public void saveAtlasAsImage(String filePath) {
        if (originalAtlasBuffer == null) {
            RainLogger.RAIN_LOGGER.error("No atlas buffer to save.");
            return;
        }

        // Create a tightly packed buffer
        ByteBuffer saveBuffer = BufferUtils.createByteBuffer(atlasSize * atlasSize * 4);

        for (int y = 0; y < atlasSize; y++) {
            for (int x = 0; x < atlasSize; x++) {
                int index = (y * atlasSize + x) * 4;

                saveBuffer.put(originalAtlasBuffer.get(index));       // R
                saveBuffer.put(originalAtlasBuffer.get(index + 1));   // G
                saveBuffer.put(originalAtlasBuffer.get(index + 2));   // B
                saveBuffer.put(originalAtlasBuffer.get(index + 3));   // A
            }
        }

        saveBuffer.flip();

        boolean result = STBImageWrite.stbi_write_png(filePath, atlasSize, atlasSize, 4, saveBuffer, atlasSize * 4);

        if (result) {
            RainLogger.RAIN_LOGGER.info("Atlas successfully saved to: {}", filePath);
        } else {
            RainLogger.RAIN_LOGGER.error("Failed to save atlas image.");
        }
    }

}
