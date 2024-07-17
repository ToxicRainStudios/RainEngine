package com.toxicrain.core.render;

import com.toxicrain.core.TextureInfo;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class BatchRenderer {

    /**The max amount of textures*/
    private static final int MAX_TEXTURES = 100;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private List<TextureVertexInfo> textureVertexInfos;
    private int vertexVboId;
    private int texCoordVboId;

    public BatchRenderer() {
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 3); // 4 vertices, 3 components each (x, y, z)
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 2); // 4 vertices, 2 components each (u, v)
        textureVertexInfos = new ArrayList<>(MAX_TEXTURES);

        // Generate VBOs
        vertexVboId = glGenBuffers();
        texCoordVboId = glGenBuffers();
    }

    private static class TextureVertexInfo {
        int textureId;
        float[] vertices;
        float[] texCoords;

        TextureVertexInfo(int textureId, float[] vertices, float[] texCoords) {
            this.textureId = textureId;
            this.vertices = vertices;
            this.texCoords = texCoords;
        }
    }

    public void beginBatch() {
        textureVertexInfos.clear();
        vertexBuffer.clear();
        texCoordBuffer.clear();
    }

    public void addTexture(TextureInfo textureInfo, float x, float y, float z) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch(); // Render the current batch if maximum is reached
            beginBatch();
        }

        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        float[] vertices = {
                x - aspectRatio, y - 1.0f, z,
                x + aspectRatio, y - 1.0f, z,
                x + aspectRatio, y + 1.0f, z,
                x - aspectRatio, y + 1.0f, z
        };

        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, vertices, texCoords));
    }

    public void renderBatch() {
        if (textureVertexInfos.isEmpty()) return;

        // Sort the textures by texture ID
        textureVertexInfos.sort(Comparator.comparingInt(t -> t.textureId));

        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        int currentTextureId = -1;
        vertexBuffer.clear();
        texCoordBuffer.clear();

        for (TextureVertexInfo info : textureVertexInfos) {
            if (info.textureId != currentTextureId) {
                // Render the current batch
                if (currentTextureId != -1) {
                    vertexBuffer.flip();
                    texCoordBuffer.flip();

                    // Upload vertex data to VBO
                    glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
                    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
                    glVertexPointer(3, GL_FLOAT, 0, 0);

                    // Upload texture coordinate data to VBO
                    glBindBuffer(GL_ARRAY_BUFFER, texCoordVboId);
                    glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_DYNAMIC_DRAW);
                    glTexCoordPointer(2, GL_FLOAT, 0, 0);

                    glDrawArrays(GL_QUADS, 0, vertexBuffer.limit() / 3);

                    vertexBuffer.clear();
                    texCoordBuffer.clear();
                }
                // Bind the new texture
                glBindTexture(GL_TEXTURE_2D, info.textureId);
                currentTextureId = info.textureId;
            }

            vertexBuffer.put(info.vertices);
            texCoordBuffer.put(info.texCoords);
        }

        // Render the last batch
        if (currentTextureId != -1) {
            vertexBuffer.flip();
            texCoordBuffer.flip();

            // Upload vertex data to VBO
            glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
            glVertexPointer(3, GL_FLOAT, 0, 0);

            // Upload texture coordinate data to VBO
            glBindBuffer(GL_ARRAY_BUFFER, texCoordVboId);
            glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_DYNAMIC_DRAW);
            glTexCoordPointer(2, GL_FLOAT, 0, 0);

            glDrawArrays(GL_QUADS, 0, vertexBuffer.limit() / 3);
        }

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisable(GL_TEXTURE_2D);

        // Unbind VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}


