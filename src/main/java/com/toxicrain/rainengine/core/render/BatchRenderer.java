package com.toxicrain.rainengine.core.render;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.eventbus.events.render.batchrenderer.BuildBatchRendererEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.batchrenderer.RenderBatchRendererEvent;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import lombok.NonNull;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * BatchRenderer optimized for single texture atlas rendering.
 */
public class BatchRenderer {

    private static final int MAX_TEXTURES = GameInfoParser.getInstance().maxTexturesPerBatch;
    private static final int VERTICES_PER_QUAD = 6;

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final FloatBuffer colorBuffer;
    private final List<TextureVertexInfo> textureVertexInfos;

    private final int vertexVboId;
    private final int texCoordVboId;
    private final int colorVboId;

    private int atlasTextureId = -1; // Store atlas texture ID after first addTexture call

    public BatchRenderer() {
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 3);
        texCoordBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 2);
        colorBuffer = BufferUtils.createFloatBuffer(MAX_TEXTURES * VERTICES_PER_QUAD * 4);
        textureVertexInfos = new ArrayList<>(MAX_TEXTURES);

        vertexVboId = glGenBuffers();
        texCoordVboId = glGenBuffers();
        colorVboId = glGenBuffers();

        SmeagleBus.getInstance().post(new BuildBatchRendererEvent(this));
    }

    private static class TextureVertexInfo {
        float[] vertices;
        float[] texCoords;
        float[] colors;

        TextureVertexInfo(float[] vertices, float[] texCoords, float[] colors) {
            this.vertices = vertices;
            this.texCoords = texCoords;
            this.colors = colors;
        }
    }

    public void beginBatch() {
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);

        textureVertexInfos.clear();
        vertexBuffer.clear();
        texCoordBuffer.clear();
        colorBuffer.clear();

        atlasTextureId = -1;
    }

    public void addTexture(@NonNull TextureRegion region, float x, float y, float z, TileParameters params) {
        if (willOverflow()) {
            renderBatch();
            beginBatch();
        }

        // Capture atlas texture ID on first add
        if (atlasTextureId == -1) {
            atlasTextureId = region.getTextureInfo().textureId;
        }

        float angle = params.angle != null ? params.angle : calculateRotationAngle(x, y, params.posX, params.posY);
        float[] rotatedVertices = createRotatedVertices(region.getTextureInfo(), x, y, z, angle, params.scaleX, params.scaleY);
        float[] triangleVertices = generateTriangleVertices(rotatedVertices);
        float[] texCoords = createTexCoords(region);
        float[] triangleTexCoords = generateTriangleTexCoords(texCoords);

        float[] color = params.color != null ? params.color : (params.lightPositions != null
                ? determineColorBasedOnLightLevel(calculateLightLevel(params.lightPositions, rotatedVertices))
                : new float[]{1f, 1f, 1f, 1f});
        float[] triangleColors = generateTriangleColors(color);

        textureVertexInfos.add(new TextureVertexInfo(triangleVertices, triangleTexCoords, triangleColors));
    }

    private boolean willOverflow() {
        int requiredVertices = (textureVertexInfos.size() + 1) * VERTICES_PER_QUAD;
        return requiredVertices > MAX_TEXTURES * VERTICES_PER_QUAD;
    }

    private float[] createRotatedVertices(com.toxicrain.rainengine.texture.TextureInfo textureInfo, float x, float y, float z,
                                          float angle, float scaleX, float scaleY) {
        float aspectRatio = (float) textureInfo.width / textureInfo.height;
        float[] originalVertices = {
                -aspectRatio * scaleX, -scaleY, 0f,
                aspectRatio * scaleX, -scaleY, 0f,
                aspectRatio * scaleX, scaleY, 0f,
                -aspectRatio * scaleX, scaleY, 0f
        };

        float cosTheta = (float) Math.cos(angle);
        float sinTheta = (float) Math.sin(angle);

        float[] rotatedVertices = new float[12];
        for (int i = 0; i < 4; i++) {
            int idx = i * 3;
            float vx = originalVertices[idx];
            float vy = originalVertices[idx + 1];
            rotatedVertices[idx] = x + (vx * cosTheta - vy * sinTheta);
            rotatedVertices[idx + 1] = y + (vx * sinTheta + vy * cosTheta);
            rotatedVertices[idx + 2] = z;
        }
        return rotatedVertices;
    }

    private float[] generateTriangleVertices(float[] rotatedVertices) {
        return new float[]{
                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[3], rotatedVertices[4], rotatedVertices[5],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],

                rotatedVertices[0], rotatedVertices[1], rotatedVertices[2],
                rotatedVertices[6], rotatedVertices[7], rotatedVertices[8],
                rotatedVertices[9], rotatedVertices[10], rotatedVertices[11]
        };
    }

    private float[] createTexCoords(TextureRegion region) {
        return new float[]{
                region.getU0(), region.getV0(),
                region.getU1(), region.getV0(),
                region.getU1(), region.getV1(),
                region.getU0(), region.getV1()
        };
    }

    private float[] generateTriangleTexCoords(float[] texCoords) {
        return new float[]{
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
        float[] minLight = {0.03f, 0.03f, 0.03f, 1f};
        float[] maxLight = {1f, 1f, 1f, 1f};
        lightLevel = Math.max(0f, Math.min(lightLevel, 1f));
        float[] result = new float[4];
        for (int i = 0; i < 4; i++) {
            result[i] = minLight[i] + (maxLight[i] - minLight[i]) * lightLevel;
        }
        return result;
    }

    private float calculateLightLevel(List<float[]> lightPositions, float[] vertices) {
        float totalLight = 0f;
        for (float[] light : lightPositions) {
            float lx = light[0];
            float ly = light[1];
            float maxDist = light[2];

            for (int i = 0; i < vertices.length; i += 3) {
                float vx = vertices[i];
                float vy = vertices[i + 1];
                float dx = lx - vx;
                float dy = ly - vy;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float intensity = Math.max(0, 1 - dist / maxDist);
                totalLight += intensity;
            }
        }
        int vertCount = vertices.length / 3;
        return Math.min(1f, totalLight / vertCount);
    }

    public void renderBatch() {
        if (textureVertexInfos.isEmpty() || atlasTextureId == -1) return;

        vertexBuffer.clear();
        texCoordBuffer.clear();
        colorBuffer.clear();

        SmeagleBus.getInstance().post(new RenderBatchRendererEvent(this));

        // Bind the atlas texture once
        glBindTexture(GL_TEXTURE_2D, atlasTextureId);

        for (TextureVertexInfo info : textureVertexInfos) {
            vertexBuffer.put(info.vertices);
            texCoordBuffer.put(info.texCoords);
            colorBuffer.put(info.colors);
        }

        vertexBuffer.flip();
        texCoordBuffer.flip();
        colorBuffer.flip();

        renderCurrentBatch();

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_COLOR_ARRAY);
        glDisable(GL_TEXTURE_2D);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void renderCurrentBatch() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindBuffer(GL_ARRAY_BUFFER, vertexVboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, texCoordVboId);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_DYNAMIC_DRAW);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_DYNAMIC_DRAW);
        glColorPointer(4, GL_FLOAT, 0, 0);

        glDrawArrays(GL_TRIANGLES, 0, vertexBuffer.limit() / 3);
    }
}
