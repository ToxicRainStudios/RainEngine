package com.toxicrain.util;

import com.toxicrain.core.Logger;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.PackInfoParser;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureUtils {
    public static TextureInfo floorTexture;
    public static TextureInfo playerTexture;
    public static TextureInfo splatterTexture;
    public static TextureInfo concreteTexture1;
    public static TextureInfo concreteTexture2;
    public static TextureInfo missingTexture;


    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        concreteTexture1 = loadTexture(PackInfoParser.concreteTexture1);
        missingTexture = loadTexture(PackInfoParser.missingTexture);
        floorTexture = loadTexture(PackInfoParser.floorTexture);
        playerTexture = loadTexture(PackInfoParser.playerTexture);
        splatterTexture = loadTexture(PackInfoParser.splatterTexture);
        concreteTexture2 = loadTexture(PackInfoParser.concreteTexture2);

    }

    public static TextureInfo loadTexture(String filePath) {
        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Load the image with RGBA channels
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

        // Generate and configure the texture
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Free the image memory
        stbi_image_free(image);

        return new TextureInfo(textureId, width, height);
    }
}
