package com.toxicrain.rainengine.core.render.rendering;

import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;

/**
 * Interface for a render pass.
 */
public interface RenderPass {
    void render(BatchRenderer batchRenderer, Camera camera);
}
