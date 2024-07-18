package com.toxicrain.core.render;

import com.toxicrain.core.TextureInfo;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * The BatchRenderer class handles rendering multiple textures in a batch
 * to improve performance by reducing the number of draw calls.
 */
public class BatchRenderer {

    /** The max amount of textures */
    private static final int MAX_TEXTURES = 100;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final List<TextureVertexInfo> textureVertexInfos;
    private final int vertexVboId;
    private final int texCoordVboId;

    /**
     * Constructs a BatchRenderer and initializes the vertex and texture coordinate buffers
     * as well as the Vertex Buffer Objects (VBOs).
     */
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

        /**
         * Constructs a TextureVertexInfo with the specified texture ID, vertices, and texture coordinates.
         *
         * @param textureId the ID of the texture
         * @param vertices the vertex coordinates of the texture
         * @param texCoords the texture coordinates
         */
        TextureVertexInfo(int textureId, float[] vertices, float[] texCoords) {
            this.textureId = textureId;
            this.vertices = vertices;
            this.texCoords = texCoords;
        }
    }

    /**
     * Begins a new batch for rendering. Clears the current list of texture vertex infos
     * and resets the buffers.
     */
    public void beginBatch() {
        textureVertexInfos.clear();
        vertexBuffer.clear();
        texCoordBuffer.clear();
    }

    /**
     * Adds a texture with specified rotation to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     */
    public void addTexture(TextureInfo textureInfo, float x, float y, float z, float mouseX, float mouseY) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch(); // Render the current batch if maximum is reached
            beginBatch();
        }

        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        // Calculate angle relative to the mouse position
        float dx = mouseX - x;
        float dy = mouseY - y;
        float angle = (float) Math.atan2(dy, dx);

        // Original vertices without rotation
        float[] originalVertices = {
                -aspectRatio, -1.0f, 0.0f,
                aspectRatio, -1.0f, 0.0f,
                aspectRatio, 1.0f, 0.0f,
                -aspectRatio, 1.0f, 0.0f
        };

        // Rotate the vertices
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);
        float[] rotatedVertices = new float[12];

        for (int i = 0; i < 4; i++) {
            float vx = originalVertices[i * 3];
            float vy = originalVertices[i * 3 + 1];
            rotatedVertices[i * 3] = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[i * 3 + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[i * 3 + 2] = z;
        }

        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, rotatedVertices, texCoords));
    }

    /**
     * Adds a texture without rotation to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     */
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

    /**
     * Renders the current batch of textures. Uploads vertex and texture coordinate data
     * to the GPU and issues draw calls.
     */
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


