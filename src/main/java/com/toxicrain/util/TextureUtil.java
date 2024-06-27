package com.toxicrain.util;

import com.toxicrain.core.TextureInfo;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureUtil {
    public static int exampleTextureID;
    public static int floorTextureID;

    public static TextureInfo exampleTexture;
    public static TextureInfo floorTexture;

    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        exampleTexture = loadTexture("C:\\Users\\hudso\\OneDrive\\Pictures\\Capture.png");
        floorTexture = loadTexture("C:\\Users\\hudso\\Downloads\\floor.png");

        exampleTextureID = exampleTexture.textureId;
        floorTextureID = floorTexture.textureId;
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

        // Free the image memory
        stbi_image_free(image);

        return new TextureInfo(textureId, width, height);
    }



}
