package com.toxicrain.rainengine.texture;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {

    private final Map<Resource, TextureRegion> regionMap;
    @Getter private final int atlasTextureId;
    @Getter private final int atlasSize;

    private ByteBuffer atlasBuffer; // Store the atlas buffer for saving

    public TextureAtlas(int atlasSize) {
        this.atlasSize = atlasSize;
        this.atlasTextureId = glGenTextures();
        this.regionMap = new HashMap<>(256);
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

            if (imagePaths.isEmpty()) {
                RainLogger.RAIN_LOGGER.warn("No textures found for atlas: {}", directory);
                return;
            }

            // Allocate atlas buffer
            atlasBuffer = BufferUtils.createByteBuffer(atlasSize * atlasSize * 4);

            int shelfX = 0;
            int shelfY = 0;
            int shelfHeight = 0;

            // Single stack allocation for all image loads
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                IntBuffer channelsBuffer = stack.mallocInt(1);

                for (Path path : imagePaths) {
                    String filePath = path.toString();
                    Resource resource = Resource.fromFile(directory, path);

                    widthBuffer.clear();
                    heightBuffer.clear();
                    channelsBuffer.clear();

                    ByteBuffer image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
                    if (image == null) {
                        throw new RuntimeException("Failed to load texture: "
                                + filePath + " - " + stbi_failure_reason());
                    }

                    int imageWidth = widthBuffer.get(0);
                    int imageHeight = heightBuffer.get(0);

                    // New shelf if image doesn't fit in current row
                    if (shelfX + imageWidth > atlasSize) {
                        shelfY += shelfHeight;
                        shelfX = 0;
                        shelfHeight = 0;
                    }

                    // Check overflow
                    if (shelfY + imageHeight > atlasSize) {
                        stbi_image_free(image);
                        throw new RuntimeException(
                                "Texture atlas overflow. Increase atlas size or use better packing."
                        );
                    }

                    // FAST NATIVE COPY (memcpy per row)
                    copyImageToAtlasNative(
                            atlasBuffer,
                            atlasSize,
                            image,
                            imageWidth,
                            imageHeight,
                            shelfX,
                            shelfY
                    );

                    float u0 = (float) shelfX / atlasSize;
                    float v0 = (float) shelfY / atlasSize;
                    float u1 = (float) (shelfX + imageWidth) / atlasSize;
                    float v1 = (float) (shelfY + imageHeight) / atlasSize;

                    boolean hasTransparency = checkTransparencyFast(image);

                    TextureInfo textureInfo = new TextureInfo(
                            atlasTextureId,
                            atlasSize,
                            atlasSize,
                            hasTransparency
                    );

                    TextureRegion region = new TextureRegion(textureInfo, u0, v0, u1, v1);
                    regionMap.put(resource, region);

                    shelfX += imageWidth;
                    shelfHeight = Math.max(shelfHeight, imageHeight);

                    stbi_image_free(image);
                }
            }

            uploadAtlasToGPU();

        } catch (IOException e) {
            throw new RuntimeException("Failed to build texture atlas.", e);
        }
    }

    /**
     * Ultra-fast native blit using LWJGL memCopy (row by row).
     * Avoids lots of ByteBuffer get/put calls.
     */
    private void copyImageToAtlasNative(
            ByteBuffer atlas,
            int atlasSize,
            ByteBuffer image,
            int imageWidth,
            int imageHeight,
            int xOffset,
            int yOffset
    ) {
        long atlasAddress = MemoryUtil.memAddress(atlas);
        long imageAddress = MemoryUtil.memAddress(image);

        int srcStride = imageWidth * 4;   // RGBA
        int dstStride = atlasSize * 4;    // RGBA atlas row

        for (int y = 0; y < imageHeight; y++) {
            long src = imageAddress + (long) y * srcStride;
            long dst = atlasAddress + (long) ((yOffset + y) * dstStride + xOffset * 4);

            MemoryUtil.memCopy(src, dst, srcStride);
        }
    }

    private void uploadAtlasToGPU() {
        glBindTexture(GL_TEXTURE_2D, atlasTextureId);

        // Ensure tight packing (important for performance & correctness)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                atlasSize,
                atlasSize,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                atlasBuffer
        );

        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        RainLogger.RAIN_LOGGER.info("Texture atlas uploaded to GPU.");
    }

    /**
     * Fast transparency check using stride iteration
     */
    private boolean checkTransparencyFast(ByteBuffer image) {
        int capacity = image.capacity();
        for (int i = 3; i < capacity; i += 4) { // Check alpha channel only
            if ((image.get(i) & 0xFF) < 255) {
                return true;
            }
        }
        return false;
    }

    public TextureRegion getRegion(Resource resource) {
        return regionMap.get(resource);
    }

    /**
     * Saves the current texture atlas to an image file.
     *
     * @param filePath the path to save the image.
     */
    public void saveAtlasAsImage(String filePath) {
        if (atlasBuffer == null) {
            RainLogger.RAIN_LOGGER.error("No atlas buffer to save.");
            return;
        }

        boolean result = STBImageWrite.stbi_write_png(
                filePath,
                atlasSize,
                atlasSize,
                4,
                atlasBuffer,
                atlasSize * 4
        );

        if (result) {
            RainLogger.RAIN_LOGGER.info("Atlas successfully saved to: {}", filePath);
        } else {
            RainLogger.RAIN_LOGGER.error("Failed to save atlas image to {}", filePath);
        }
    }
}