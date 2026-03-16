package com.toxicrain.rainengine.core.render.rendering;

import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final List<RenderPass> passes = new ArrayList<>();
    private final BatchRenderer batchRenderer;

    public Renderer(BatchRenderer batchRenderer) {
        this.batchRenderer = batchRenderer;
    }

    /**
     * Add a render pass (e.g., tiles, NPCs, GUI)
     */
    public void addPass(RenderPass pass) {
        passes.add(pass);
    }

    /**
     * Remove a render pass (e.g., tiles, NPCs, GUI)
     */
    public void removePass(RenderPass pass) {
        passes.remove(pass);
    }

    /**
     * Render all passes using the provided camera.
     */
    public void render(Camera camera) {
        // Clear buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Setup view matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);
        camera.getViewMatrix().get(viewBuffer);
        glLoadMatrixf(viewBuffer);

        // Execute all render passes
        for (RenderPass pass : passes) {
            batchRenderer.beginBatch();
            pass.render(batchRenderer, camera);
            batchRenderer.renderBatch();
        }
    }
}
