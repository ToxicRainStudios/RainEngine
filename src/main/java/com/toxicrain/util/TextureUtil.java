package com.toxicrain.util;

import com.toxicrain.core.TextureInfo;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureUtil {
    public static TextureInfo exampleTexture;
    public static TextureInfo floorTexture;
    public static TextureInfo playerTexture;

    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        exampleTexture = loadTexture("C:\\Users\\hudso\\OneDrive\\Pictures\\Capture.png");
        floorTexture = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/floor.png");
        playerTexture = loadTexture("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/resources/images/player_shotgun_stolen.png");
    }

    public static TextureInfo loadTexture(String filePath) {
        int width;
        int height;
        ByteBuffer image;

        // Load image using STBImage
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        // Generate a texture ID
        int textureId = glGenTextures();
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        // Generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);

        // Set texture parameters (these can be moved to the render method if needed)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Free the image memory
        stbi_image_free(image);

        return new TextureInfo(textureId, width, height);
    }

}
