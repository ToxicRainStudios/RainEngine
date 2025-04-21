package com.toxicrain.core.render;

import com.toxicrain.texture.TextureInfo;
import com.toxicrain.core.json.GameInfoParser;
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
    private static final int VERTICES_PER_QUAD = 6;
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
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 3); // 2 triangles per quad, 3 vertices per triangle
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 2); // 2 triangles per quad, 2 coords per vertex
        colorBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 4); // 2 triangles per quad, 4 colors per vertex
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
        // Enable necessary OpenGL states
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

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
        if (willOverflow()) {
            renderBatch();  // Flush before we overflow
            beginBatch();   // Reset state
        }

        float[] rotatedVertices = createRotatedVertices(textureInfo, x, y, z, angle, scaleX, scaleY);
        float[] triangleVertices = generateTriangleVertices(rotatedVertices);
        float[] texCoords = createTexCoords();
        float[] triangleTexCoords = generateTriangleTexCoords(texCoords);
        float[] triangleColors = generateTriangleColors(color);

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
        if (willOverflow()) {
            renderBatch();  // Flush before we overflow
            beginBatch();   // Reset state
        }
        float[] rotatedVertices = createRotatedVertices(textureInfo, x, y, z, angle, scaleX, scaleY);
        float lightLevel = calculateLightLevel(lightPositions, rotatedVertices);
        float[] color = determineColorBasedOnLightLevel(lightLevel);
        float[] triangleVertices = generateTriangleVertices(rotatedVertices);
        float[] texCoords = createTexCoords();
        float[] triangleTexCoords = generateTriangleTexCoords(texCoords);
        float[] triangleColors = generateTriangleColors(color);

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, triangleVertices, triangleTexCoords, triangleColors));
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
        if (willOverflow()) {
            renderBatch();  // Flush before we overflow
            beginBatch();   // Reset state
        }

        float angle = calculateRotationAngle(x, y, posX, posY);
        float[] rotatedVertices = createRotatedVertices(textureInfo, x, y, z, angle, scaleX, scaleY);
        float[] triangleVertices = generateTriangleVertices(rotatedVertices);
        float[] texCoords = createTexCoords();
        float[] triangleTexCoords = generateTriangleTexCoords(texCoords);
        float[] triangleColors = generateTriangleColors(color);

        textureVertexInfos.add(new TextureVertexInfo(textureInfo.textureId, triangleVertices, triangleTexCoords, triangleColors));
    }

    // Helper Methods
    private boolean willOverflow() {
        int requiredVertices = (textureVertexInfos.size() + 1) * VERTICES_PER_QUAD;
        return requiredVertices > MAX_TEXTURES * VERTICES_PER_QUAD;
    }

    private float[] createRotatedVertices(TextureInfo textureInfo, float x, float y, float z, float angle, float scaleX, float scaleY) {
        float aspectRatio = (float) textureInfo.width / textureInfo.height;
        float[] originalVertices = {
                -aspectRatio * scaleX, -scaleY, 0.0f,
                aspectRatio * scaleX, -scaleY, 0.0f,
                aspectRatio * scaleX, scaleY, 0.0f,
                -aspectRatio * scaleX, scaleY, 0.0f
        };

        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        float[] rotatedVertices = new float[12];
        for (int i = 0; i < 4; i++) {
            int index = i * 3;
            float vx = originalVertices[index];
            float vy = originalVertices[index + 1];
            rotatedVertices[index] = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[index + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[index + 2] = z;
        }
        return rotatedVertices;
    }

    private float[] generateTriangleVertices(float[] rotatedVertices) {
        return new float[] {
                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[3], rotatedVertices[4], rotatedVertices[5],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],

                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],
                rotatedVertices[9], rotatedVertices[10], rotatedVertices[11]
        };
    }

    private float[] createTexCoords() {
        return new float[] {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };
    }

    private float[] generateTriangleTexCoords(float[] texCoords) {
        return new float[] {
                texCoords[0], texCoords[1],
                texCoords[2], texCoords[3],
                texCoords[4], texCoords[5],

                texCoords[0], texCoords[1],
                texCoords[4], texCoords[5],
                texCoords[6], texCoords[7]
        };
    }

    private float[] generateTriangleColors(float[] color) {
        float[] triangleColors = new float[24];
        for (int i = 0; i < 6; i++) {
            System.arraycopy(color, 0, triangleColors, i * 4, 4);
        }
        return triangleColors;
    }

    private float calculateRotationAngle(float x, float y, float posX, float posY) {
        return (float) Math.atan2(posY - y, posX - x);
    }

    private float[] determineColorBasedOnLightLevel(float lightLevel) {
        float[] minLightColor = {0.03f, 0.03f, 0.03f, 1.0f}; // Color for minimum light level
        float[] maxLightColor = {1.0f, 1.0f, 1.0f, 1.0f}; // Color for maximum light level

        // Clamp lightLevel to the range [0, 1].
        lightLevel = Math.max(0.0f, Math.min(lightLevel, 1.0f));

        // Interpolate each color component based on the light level.
        float[] interpolatedColor = new float[4]; // Store the RGBA result
        for (int i = 0; i < 4; i++) {
            interpolatedColor[i] = minLightColor[i] + (maxLightColor[i] - minLightColor[i]) * lightLevel;
        }

        return interpolatedColor;
    }



    /**
     * Calculates the total light level at vertices based on the positions of lights and their maximum distances.
     *
     * <p>This method computes the light intensity at each vertex from multiple light sources. Each light source is
     * defined by its position (x, y) and a maximum distance that determines how far its light can reach. The intensity
     * of light at a vertex is calculated using the inverse of the distance between the vertex and the light source, with
     * the maximum value determined by the light's range. The total light level is then averaged across all vertices and
     * normalized to ensure it falls within the range [0.0, 1.0].</p>
     *
     * @param lightPositions A list of light sources, where each light source is represented by a float array
     *                       with three elements: x position, y position, and maximum distance of the light.
     * @param vertices An array of vertex coordinates, where each vertex is represented by three consecutive floats
     *                 (x, y, z). Only the x and y coordinates are used for light intensity calculation.
     *
     * @return The normalized light level at the vertices, within the range [0.0, 1.0].
     */
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
     * Renders the current batch of textures by uploading vertex, texture coordinate, and color data
     * to the GPU and issuing draw calls.
     */
    public void renderBatch() {
        // Early exit if there are no textures to render
        if (textureVertexInfos.isEmpty()) return;

        int currentTextureId = -1;

        // Clear buffers to start fresh
        vertexBuffer.clear();
        texCoordBuffer.clear();
        colorBuffer.clear();

        for (TextureVertexInfo info : textureVertexInfos) {
            // Check if we need to switch to a new texture
            if (info.textureId != currentTextureId) {
                // Render the current batch if it exists
                if (currentTextureId != -1) {
                    // Prepare buffers for rendering
                    vertexBuffer.flip();
                    texCoordBuffer.flip();
                    colorBuffer.flip();
                    renderCurrentBatch(); // Render the batch

                    // Clear buffers for the next batch
                    vertexBuffer.clear();
                    texCoordBuffer.clear();
                    colorBuffer.clear();
                }
                // Bind the new texture
                glBindTexture(GL_TEXTURE_2D, info.textureId);
                currentTextureId = info.textureId;
            }

            // Add current texture data to buffers
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

        // Disable OpenGL states and clean up
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisable(GL_TEXTURE_2D);

        // Unbind any buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void renderCurrentBatch() {
        // Make sure transparency works
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
}
