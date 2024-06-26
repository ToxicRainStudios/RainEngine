package com.toxicrain.core;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {
    private int id;
    private int width;
    private int height;

    public Texture(String fileName) throws IOException {
        ByteBuffer imageBuffer;

        // Load image using STB Image
        try {
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            IntBuffer comp = BufferUtils.createIntBuffer(1);

            // Load image file into ByteBuffer
            imageBuffer = STBImage.stbi_load(fileName, w, h, comp, 4);
            if (imageBuffer == null) {
                throw new IOException("Image file [" + fileName + "] not loaded: " + STBImage.stbi_failure_reason());
            }

            // Retrieve image dimensions
            this.width = w.get();
            this.height = h.get();

        } catch (Exception e) {
            throw new IOException("Failed to load image file: " + fileName, e);
        }

        // Generate OpenGL texture
        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        STBImage.stbi_image_free(imageBuffer);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void cleanup() {
        glDeleteTextures(id);
    }
}



