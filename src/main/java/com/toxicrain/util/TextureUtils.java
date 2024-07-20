package com.toxicrain.util;

import com.toxicrain.core.Logger;
import com.toxicrain.core.TextureInfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureUtils {
    public static TextureInfo exampleTexture;
    public static TextureInfo floorTexture;
    public static TextureInfo playerTexture;
    public static TextureInfo splatterTexture;
    public static TextureInfo concreteTexture1;
    public static TextureInfo concreteTexture2;


    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        exampleTexture = loadTexture("C:\\Users\\hudso\\OneDrive\\Pictures\\Capture.png");
        floorTexture = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/floor.png");
        playerTexture = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/player_shotgun_stolen.png");
        splatterTexture = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/splatter.png");
        concreteTexture1 = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/floor_concrete.png");
        concreteTexture2 = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/floor_concrete2.png");


    }

    public static TextureInfo loadTexture(String filePath) {
        int width;
        int height;
        ByteBuffer image;

        // Load image using STBImage
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Load image with 4 channels (RGBA)
            image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filePath + " - " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
            long fileSize = FileUtils.getFileSize(filePath);
            Logger.printLOG("Loaded texture: " + filePath + " (Width: " + width + ", Height: " + height + ", File Size: (bytes) " + fileSize + ")");
        }

        // Generate a texture ID
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        // Generate mipmaps
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
