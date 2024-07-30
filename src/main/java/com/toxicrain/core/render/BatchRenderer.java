package com.toxicrain.core.render;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.util.Color;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * The BatchRenderer class handles rendering multiple textures in a batch
 * to improve performance by reducing the number of draw calls.
 *
 * @author strubium
 */
public class BatchRenderer {

    /** The maximum number of textures per batch */
    private static final int MAX_TEXTURES = GameInfoParser.maxTexturesPerBatch;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final FloatBuffer colorBuffer;
    private final List<TextureVertexInfo> textureVertexInfos;
    private final int vertexVboId;
    private final int texCoordVboId;
    private final int colorVboId;


    /**
     * Constructs a BatchRenderer and initializes the vertex, texture coordinate, and color buffers
     * as well as the Vertex Buffer Objects (VBOs).
     */
    public BatchRenderer() {
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 6 * 3); // 2 triangles per quad, 3 vertices per triangle
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 6 * 2); // 2 triangles per quad, 2 coords per vertex
        colorBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * 6 * 4); // 2 triangles per quad, 4 colors per vertex
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

    public void beginBatch() {
        textureVertexInfos.clear();
        vertexBuffer.clear();
        texCoordBuffer.clear();
        colorBuffer.clear();
    }

    /**
     * Adds a texture with specified rotation, scaling, and color to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     * @param angle the rotation angle in radians
     * @param scaleX the scale factor along the x-axis
     * @param scaleY the scale factor along the y-axis
     * @param color the color tint as a float array (RGBA)
     */
    public void addTexture(TextureInfo textureInfo, float x, float y, float z, float angle, float scaleX, float scaleY, float[] color) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch();
            beginBatch();
        }

        // Calculate aspect ratio and create original vertices
        float aspectRatio = (float) textureInfo.width / textureInfo.height;
        float[] originalVertices = {
                -aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, scaleY, 0.0f,
                -aspectRatio * scaleX, scaleY, 0.0f
        };

        // Calculate rotation
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        // Rotate vertices
        float[] rotatedVertices = new float[12];
        for (int i = 0; i < 4; i++) {
            int index = i * 3;
            float vx = originalVertices[index];
            float vy = originalVertices[index + 1];
            rotatedVertices[index]     = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[index + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[index + 2] = z;
        }

        // Texture coordinates for the two triangles
        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        // Generate texture coordinate and color data
        float[] triangleVertices = {
                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[3], rotatedVertices[4], rotatedVertices[5],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],

                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],
                rotatedVertices[9], rotatedVertices[10], rotatedVertices[11]
        };

        float[] triangleTexCoords = {
                texCoords[0], texCoords[1],
                texCoords[2], texCoords[3],
                texCoords[4], texCoords[5],

                texCoords[0], texCoords[1],
                texCoords[4], texCoords[5],
                texCoords[6], texCoords[7]
        };

        float[] triangleColors = new float[24];
        for (int i = 0; i < 6; i++) {
            int baseIndex = i * 4;
            System.arraycopy(color, 0, triangleColors, baseIndex, 4);
        }

        // Add the texture vertex info
        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, triangleVertices, triangleTexCoords, triangleColors));
    }

    /**
     * Adds a lit texture with specified rotation and scaling to the current batch.
     * If the batch exceeds the maximum texture count, it is rendered and a new batch is started.
     *
     * @param textureInfo the texture information
     * @param x the x-coordinate of the texture
     * @param y the y-coordinate of the texture
     * @param z the z-coordinate of the texture
     * @param angle the rotation angle in radians
     * @param scaleX the scale factor along the x-axis
     * @param scaleY the scale factor along the y-axis
     * @param lightPositions the list of light positions
     */
    public void addTextureLit(TextureInfo textureInfo, float x, float y, float z, float angle, float scaleX, float scaleY, List<float[]> lightPositions) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch();
            beginBatch();
        }

        // Calculate aspect ratio and create original vertices
        float aspectRatio = (float) textureInfo.width / textureInfo.height;
        float[] originalVertices = {
                -aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, scaleY, 0.0f,
                -aspectRatio * scaleX, scaleY, 0.0f
        };

        // Calculate rotation
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        // Rotate vertices
        float[] rotatedVertices = new float[12];
        for (int i = 0; i < 4; i++) {
            int index = i * 3;
            float vx = originalVertices[index];
            float vy = originalVertices[index + 1];
            rotatedVertices[index]     = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[index + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[index + 2] = z;
        }

        // Texture coordinates for the two triangles
        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        // Calculate light level
        float lightLevel = calculateLightLevel(lightPositions, rotatedVertices);

        // Generate color based on light level
        float[] color = Color.toFloatArray(Color.LIGHT_LEVEL_1); // Default to the lowest light level
        if (lightLevel >= 1.0f) {
            color = Color.toFloatArray(Color.LIGHT_LEVEL_20); // Highest light level
        } else if (lightLevel > 0) {
            int level = (int) (lightLevel * 19) + 1;
            color = Color.toFloatArray(Color.values()[level + Color.LIGHT_LEVEL_1.ordinal()]);
        }

        // Generate vertex, texture coordinate, and color data
        float[] triangleVertices = {
                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[3], rotatedVertices[4], rotatedVertices[5],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],

                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],
                rotatedVertices[9], rotatedVertices[10], rotatedVertices[11]
        };

        float[] triangleTexCoords = {
                texCoords[0], texCoords[1],
                texCoords[2], texCoords[3],
                texCoords[4], texCoords[5],

                texCoords[0], texCoords[1],
                texCoords[4], texCoords[5],
                texCoords[6], texCoords[7]
        };

        float[] triangleColors = new float[24];
        for (int i = 0; i < 6; i++) {
            int baseIndex = i * 4;
            System.arraycopy(color, 0, triangleColors, baseIndex, 4);
        }

        // Add the texture vertex info
        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, triangleVertices, triangleTexCoords, triangleColors));
    }

    private float calculateLightLevel(List<float[]> lightPositions, float[] vertices) {
        float totalLightLevel = 0.0f;
        for (float[] lightPos : lightPositions) {
            float lightX = lightPos[0];
            float lightY = lightPos[1];
            float maxDistance = lightPos[2]; // Max distance for this light

            // Calculate the light intensity for each vertex
            for (int i = 0; i < vertices.length; i += 3) {
                float vertexX = vertices[i];
                float vertexY = vertices[i + 1];
                float dx = lightX - vertexX;
                float dy = lightY - vertexY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float intensity = Math.max(0, 1 - distance / maxDistance);
                totalLightLevel += intensity;
            }
        }

        // Normalize the total light level
        int vertexCount = vertices.length / 3;
        return Math.min(1.0f, totalLightLevel / vertexCount);
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
     * @param scaleX a scale modifier for the x-axis
     * @param scaleY a scale modifier for the y-axis
     * @param color the color to apply to the texture (RGBA)
     */
    public void addTexturePos(TextureInfo textureInfo, float x, float y, float z, float posX, float posY, float scaleX, float scaleY, float[] color) {
        if (textureVertexInfos.size() >= MAX_TEXTURES) {
            renderBatch();
            beginBatch();
        }

        // Calculate aspect ratio
        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        // Calculate rotation angle
        float dx = posX - x;
        float dy = posY - y;
        float angle = (float) Math.atan2(dy, dx);
        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        // Calculate aspect ratio and create original vertices
        float[] originalVertices = {
                -aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, -1.0f * scaleY, 0.0f,
                aspectRatio * scaleX, scaleY, 0.0f,
                -aspectRatio * scaleX, scaleY, 0.0f
        };

        // Rotate and translate vertices
        float[] rotatedVertices = new float[12];
        for (int i = 0; i < 4; i++) {
            int index = i * 3;
            float vx = originalVertices[index];
            float vy = originalVertices[index + 1];
            rotatedVertices[index]     = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[index + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[index + 2] = z;

        }

        // Texture coordinates (assuming no scaling)
        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };

        // Repeat the color for each vertex
        float[] colors = new float[24];
        for (int i = 0; i < 4; i++) {
            int baseIndex = i * 4;
            System.arraycopy(color, 0, colors, baseIndex, 4);
        }

        // Define the vertices for two triangles
        float[] triangleVertices = {
                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[3], rotatedVertices[4], rotatedVertices[5],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],

                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],
                rotatedVertices[9], rotatedVertices[10], rotatedVertices[11]
        };

        // Define the texture coordinates for the two triangles
        float[] triangleTexCoords = {
                texCoords[0], texCoords[1],
                texCoords[2], texCoords[3],
                texCoords[4], texCoords[5],

                texCoords[0], texCoords[1],
                texCoords[4], texCoords[5],
                texCoords[6], texCoords[7]
        };

        // Define the colors for the two triangles
        float[] triangleColors = {
                colors[0], colors[1], colors[2], colors[3],
                colors[4], colors[5], colors[6], colors[7],
                colors[8], colors[9], colors[10], colors[11],

                colors[0], colors[1], colors[2], colors[3],
                colors[8], colors[9], colors[10], colors[11],
                colors[12], colors[13], colors[14], colors[15]
        };

        // Add the texture information to the batch
        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, triangleVertices, triangleTexCoords, triangleColors));
    }

    /**
     * Renders the current batch of textures. Uploads vertex, texture coordinate, and color data
     * to the GPU and issues draw calls.
     */
    public void renderBatch() {
        if (textureVertexInfos.isEmpty()) return;

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

        glDrawArrays(GL_TRIANGLES, 0, vertexBuffer.limit() / 3);
    }

    /**
     * Enables or disables blending.
     *
     * @param enabled true to enable blending, false to disable
     */
    public void setBlendingEnabled(boolean enabled) {
        if (enabled) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        } else {
            glDisable(GL_BLEND);
        }
    }
}
