package com.toxicrain.core;

import static org.lwjgl.opengl.GL30.*;

public class Quad {
    private int vaoId;
    private int vboId;
    private int eboId;
    private int vertexCount;
    public TextureInfo textureInfo;

    public void createQuad(TextureInfo textureInfo) {
        float width = textureInfo.width;
        float height = textureInfo.height;

        float[] vertices = {
                // Positions        // Texture Coordinates
                0.0f, 0.0f,         0.0f, 0.0f,
                0.0f, height,       0.0f, 1.0f,
                width, height,      1.0f, 1.0f,
                width, 0.0f,        1.0f, 0.0f
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        vertexCount = indices.length;

        // Create VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute pointer
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture coordinates attribute pointer
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Create EBO
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Unbind VAO
        glBindVertexArray(0);
    }

    public void render() {
        glBindTexture(GL_TEXTURE_2D, textureInfo.textureId);

        // Bind VAO
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        // Unbind VAO
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
    }
}
