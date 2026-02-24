package com.toxicrain.rainengine.core.render.rendering.renderpass;

import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.registries.manager.ArtifactManager;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.core.render.rendering.RenderPass;

public class NPCRenderPass implements RenderPass {

    @Override
    public void render(BatchRenderer batchRenderer, Camera camera) {
        ArtifactManager.getInstance().render(batchRenderer);
    }
}