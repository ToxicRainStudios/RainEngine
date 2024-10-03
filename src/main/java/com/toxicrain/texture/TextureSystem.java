package com.toxicrain.texture;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.PackInfoParser;
import com.toxicrain.util.FileUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureSystem {
    public static TextureInfo floorTexture;
    public static TextureInfo playerTexture;
    public static TextureInfo splatterTexture;
    public static TextureInfo concreteTexture1;
    public static TextureInfo concreteTexture2;
    public static TextureInfo missingTexture;
    public static TextureInfo dirtTexture1;
    public static TextureInfo dirtTexture2;
    public static TextureInfo grassTexture1;

    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        concreteTexture1 = loadTexture(PackInfoParser.concreteTexture1);
        missingTexture = loadTexture(PackInfoParser.missingTexture);
        floorTexture = loadTexture(PackInfoParser.floorTexture);
        playerTexture = loadTexture(PackInfoParser.playerTexture);
        splatterTexture = loadTexture(PackInfoParser.splatterTexture);
        concreteTexture2 = loadTexture(PackInfoParser.concreteTexture2);
        dirtTexture1 = loadTexture((PackInfoParser.dirtTexture1));
        dirtTexture2 = loadTexture((PackInfoParser.dirtTexture2));
        grassTexture1 = loadTexture((PackInfoParser.grassTexture1));
    }

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
            Logger.printLOG(String.format("Loaded texture: %s (Width: %d, Height: %d, File Size: %d bytes)", filePath, width, height, fileSize));
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
