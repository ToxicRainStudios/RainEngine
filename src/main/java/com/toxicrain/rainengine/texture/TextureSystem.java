package com.toxicrain.rainengine.texture;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.resources.ResourceManager;
import com.toxicrain.rainengine.util.FileUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureSystem {

    /**
     * Load all textures from the /images folder into the ResourceManager
     */
    public static void initTextures() {
        String textureDirectory = FileUtils.getCurrentWorkingDirectory("resources/images");

        try {
            Files.walk(Paths.get(textureDirectory))
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .forEach(path -> {
                        String filePath = path.toString();
                        Resource location = Resource.fromFile(textureDirectory, path);

                        try {
                            ResourceManager.load(TextureInfo.class, location, filePath);
                        } catch (Exception e) {
                            RainLogger.RAIN_LOGGER.error("Failed to load texture: {}", path.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load textures from directory: " + textureDirectory, e);
        }

        RainLogger.RAIN_LOGGER.info("Texture loading complete.");
    }

    /**
     * Retrieve a texture by its Resource
     */
    public static TextureInfo getTexture(Resource location) {
        TextureInfo texture = ResourceManager.get(TextureInfo.class, location);
        if (texture == null) {
            RainLogger.RAIN_LOGGER.error("Texture not found: {}", location);
            return ResourceManager.get(TextureInfo.class, new Resource("rainengine:missing")); // Fallback texture
        }
        return texture;
    }

    /**
     * Retrieve a texture by its string path (like "core:textures/brick")
     */
    public static TextureInfo getTexture(String location) {
        return getTexture(new Resource(location));
    }

    /**
     * Loads a texture from file
     */
    public static TextureInfo loadTexture(String filePath) {
        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filePath + " - " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
            long fileSize = FileUtils.getFileSize(filePath);
            RainLogger.RAIN_LOGGER.debug("Loaded texture: {} (Width: {}, Height: {}, File Size: {} bytes)", filePath, width, height, fileSize);
        } catch (Exception e) {
            throw new RuntimeException("Error loading texture: " + filePath, e);
        }

        boolean hasTransparency = checkTransparency(image, width, height);

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        try {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        } finally {
            stbi_image_free(image);
        }

        return new TextureInfo(textureId, width, height, hasTransparency);
    }

    /**
     * Check if the texture contains transparency by scanning its alpha channel.
     * Assumes the image data is in RGBA format (4 bytes per pixel).
     *
     * @param image ByteBuffer containing the image data in RGBA format.
     * @param width The width of the image.
     * @param height The height of the image.
     * @return true if the texture contains transparent pixels, false otherwise.
     */
    private static boolean checkTransparency(ByteBuffer image, int width, int height) {
        int pixelCount = width * height;

        for (int i = 0; i < pixelCount; i++) {
            int alpha = image.get(i * 4 + 3) & 0xFF;
            if (alpha < 255) {
                return true;
            }
        }

        return false;
    }

    public static void reloadTextures() {
        RainLogger.RAIN_LOGGER.info("Texture reload requested.");
        ResourceManager.reload(TextureInfo.class);
        RainLogger.RAIN_LOGGER.info("Texture reload complete.");
    }
}
