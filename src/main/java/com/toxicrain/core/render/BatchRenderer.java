package com.toxicrain.core.render;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.GameInfoParser;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * The BatchRenderer class handles rendering multiple textures in a batch
 * to improve performance by reducing the number of draw calls.
 */
public class BatchRenderer {

    /** The max amount of textures */
    private static final int MAX_TEXTURES = GameInfoParser.maxTexturesPerBatch;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final FloatBuffer colorBuffer;
    private final List<TextureVertexInfo> textureVertexInfos;
    private final int vertexVboId;
    private final int texCoordVboId;
    private final int colorVboId;

    private boolean blendingEnabled = false;

    /**
     * Constructs a BatchRenderer and initializes the vertex, texture coordinate, and color buffers
     * as well as the Vertex Buffer Objects (VBOs).
     */
    public BatchRenderer() {
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 3); // 4 vertices, 3 components each (x, y, z)
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 2); // 4 vertices, 2 components each (u, v)
        colorBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 4 * 4); // 4 vertices, 4 components each (r, g, b, a)
        textureVertexInfos = new ArrayList<>(MAX_TEXTURES);

        // Generate VBOs
        vertexVboId = glGenBuffers();
        texCoordVboId = glGenBuffers();
        colorVboId = glGenBuffers();
    }

    private static class TextureVertexInfo {
        int textureId;
        float[] vertices;
        float[] texCoords;
        float[] colors;

        /**
         * Constructs a TextureVertexInfo with the specified texture ID, vertices, texture coordinates, and colors.
         *
         * @param textureId the ID of the texture
         * @param vertices the vertex coordinates of the texture
         * @param texCoords the texture coordinates
         * @param colors the color data for the vertices
         */
        TextureVertexInfo(int textureId, float[] vertices, float[] texCoords, float[] colors) {
            this.textureId = textureId;
            this.vertices = vertices;
            this.texCoords = texCoords;
            this.colors = colors;
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
        colorBuffer.clear();
    }

    /**
     * Adds a texture with specified rotation and color to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     * @param angle the rotation angle in radians
     * @param color the color tint as a float array (RGBA)
     */
    public void addTexture(TextureInfo textureInfo, float x, float y, float z, float angle, float[] color) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch(); // Render the current batch if maximum has been reached
            beginBatch();
        }

        float aspectRatio = (float) textureInfo.width / textureInfo.height;

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

        float[] colors = new float[16];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(color, 0, colors, i * 4, 4);
        }

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, rotatedVertices, texCoords, colors));
    }

    /**
     * Adds a texture with specified rotation and color to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     * @param posX the x-coordinate of the mouse or reference point for rotation
     * @param posY the y-coordinate of the mouse or reference point for rotation
     * @param color the color to apply to the texture (RGBA)
     */
    public void addTexturePos(TextureInfo textureInfo, float x, float y, float z, float posX, float posY, float[] color) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch(); // Render the current batch if maximum has been reached
            beginBatch();
        }

        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        // Calculate angle relative to the mouse position
        float dx = posX - x;
        float dy = posY - y;
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

        float[] colors = new float[16];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(color, 0, colors, i * 4, 4);
        }

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, rotatedVertices, texCoords, colors));
    }


    /**
     * Renders the current batch of textures. Uploads vertex, texture coordinate, and color data
     * to the GPU and issues draw calls.
     */
    public void renderBatch() {
        if (textureVertexInfos.isEmpty()) return;

        // Sort the textures by texture ID
        textureVertexInfos.sort(Comparator.comparingInt(t -> t.textureId));

        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        int currentTextureId = -1;
        vertexBuffer.clear();
        texCoordBuffer.clear();
        colorBuffer.clear();

        for (TextureVertexInfo info : textureVertexInfos) {
            if (info.textureId != currentTextureId) {
                // Render the current batch if it exists
                if (currentTextureId != -1) {
                    vertexBuffer.flip();
                    texCoordBuffer.flip();
                    colorBuffer.flip();
                    renderCurrentBatch();
                    vertexBuffer.clear();
                    texCoordBuffer.clear();
                    colorBuffer.clear();
                }
                // Bind the new texture
                glBindTexture(GL_TEXTURE_2D, info.textureId);
                currentTextureId = info.textureId;
            }

            vertexBuffer.put(info.vertices);
            texCoordBuffer.put(info.texCoords);
            colorBuffer.put(info.colors);
        }

        // Render the last batch if it exists
        if (currentTextureId != -1) {
            vertexBuffer.flip();
            texCoordBuffer.flip();
            colorBuffer.flip();
            renderCurrentBatch();
        }

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisable(GL_TEXTURE_2D);

        // Unbind VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void renderCurrentBatch() {
        // Upload vertex data to VBO
        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        // Upload texture coordinate data to VBO
        glBindBuffer(GL_ARRAY_BUFFER, texCoordVboId);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_DYNAMIC_DRAW);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        // Upload color data to VBO
        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_DYNAMIC_DRAW);
        glColorPointer(4, GL_FLOAT, 0, 0);

        glDrawArrays(GL_QUADS, 0, vertexBuffer.limit() / 3);
    }

    /**
     * Enables or disables blending.
     *
     * @param enabled true to enable blending, false to disable
     */
    public void setBlendingEnabled(boolean enabled) {
        this.blendingEnabled = enabled;
    }
}



