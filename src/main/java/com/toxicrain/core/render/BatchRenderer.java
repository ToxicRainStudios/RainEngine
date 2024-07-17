package com.toxicrain.core.render;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import com.toxicrain.core.TextureInfo;
import org.lwjgl.BufferUtils;

public class BatchRenderer {
    private static final int MAX_TEXTURES = 100;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int[] textureIds;
    private int textureCount = 0;

    public BatchRenderer() {
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 3); // 4 vertices, 3 components each (x, y, z)
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 2); // 4 vertices, 2 components each (u, v)
        textureIds = new int[MAX_TEXTURES];
    }

    public void beginBatch() {
        textureCount = 0;
        vertexBuffer.clear();
        texCoordBuffer.clear();
    }

    public void addTexture(TextureInfo textureInfo) {
        if (textureCount >= MAX_TEXTURES) {
            renderBatch(); // Render the current batch if maximum is reached
            beginBatch();
        }

        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        float[] vertices = {
                -aspectRatio, -1.0f, 0.0f,
                aspectRatio, -1.0f, 0.0f,
                aspectRatio,  1.0f, 0.0f,
                -aspectRatio,  1.0f, 0.0f
        };

        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        vertexBuffer.put(vertices);
        texCoordBuffer.put(texCoords);

        textureIds[textureCount] = textureInfo.textureId;

        textureCount++;
    }

    public void renderBatch() {
        if (textureCount == 0) return;

        vertexBuffer.flip();
        texCoordBuffer.flip();

        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
        glTexCoordPointer(2, GL_FLOAT, 0, texCoordBuffer);

        for (int i = 0; i < textureCount; i++) {
            // Bind the texture for each quad
            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
            glDrawArrays(GL_QUADS, i * 4, 4);
        }

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisable(GL_TEXTURE_2D);
    }
}

