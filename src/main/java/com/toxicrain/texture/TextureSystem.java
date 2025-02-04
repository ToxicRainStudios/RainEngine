package com.toxicrain.texture;

import com.toxicrain.core.RainLogger;
import com.toxicrain.util.FileUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureSystem {

    // Map to store all loaded textures with file names
    private static final Map<String, TextureInfo> textures = new HashMap<>();

    /**
     * Init the textures by dynamically loading all images from the /images folder
     */
    public static void initTextures() {
        String textureDirectory = FileUtils.getCurrentWorkingDirectory("resources/images"); // Directory containing textures

        try {
            // Get all files in the images directory
            Files.walk(Paths.get(textureDirectory))
                    .filter(Files::isRegularFile) // Only regular files, not directories
                    .filter(path -> {
                        // Filter out files that are images (png, jpg, jpeg)
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .forEach(path -> {
                        // Load each texture
                        String filePath = path.toString();
                        TextureInfo texture = loadTexture(filePath);
                        if (texture != null) {
                            // Store the texture with its file name (without extension) as the key
                            String textureName = path.getFileName().toString().replaceFirst("[.][^.]+$", ""); // remove extension
                            textures.put(textureName, texture);
                            RainLogger.printLOG("Loaded texture: " + textureName);
                        } else {
                            RainLogger.printERROR("Failed to load texture: " + path.getFileName());
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load textures from directory: " + textureDirectory, e);
        }

        RainLogger.printLOG(String.format("Loaded %d textures.", textures.size()));
    }

    /**
     * Retrieve a texture by its name (without extension)
     *
     * @param textureName Name of the texture file (without extension)
     * @return TextureInfo object for the corresponding texture, or null if not found
     */
    public static TextureInfo getTexture(String textureName) {
        if (!textures.containsKey(textureName)) {
            RainLogger.printLOG("Texture not found: " + textureName);
            return null;  // Return null or throw an exception if texture is not found
        }
        return textures.get(textureName);
    }

    /**
     * Load a texture from a file path
     *
     * @param filePath Path to the image file
     * @return TextureInfo containing texture data
     */
    public static TextureInfo loadTexture(String filePath) {
        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Load the image with RGBA channels (4 channels)
            image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filePath + " - " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
            long fileSize = FileUtils.getFileSize(filePath);
            RainLogger.rainLogger.debug("Loaded texture: {} (Width: {}, Height: {}, File Size: {} bytes)", filePath, width, height, fileSize);
        } catch (Exception e) {
            throw new RuntimeException("Error loading texture: " + filePath, e);
        }

        // Detect transparency in the texture
        boolean hasTransparency = checkTransparency(image, width, height);

        // Generate and configure the texture
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        try {
            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        } finally {
            // Free the image memory
            stbi_image_free(image);
        }

        // Return the TextureInfo with the transparency information
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
            // The pixel data is stored in RGBA format, so we access the 4th byte for each pixel.
            // We use & 0xFF to convert signed byte to unsigned int (0-255).
            int alpha = image.get(i * 4 + 3) & 0xFF;  // 4th byte of each pixel (alpha channel)

            if (alpha < 255) {  // Check if alpha is less than fully opaque
                return true;  // Texture contains transparency
            }
        }

        return false;  // No transparency detected
    }
}
