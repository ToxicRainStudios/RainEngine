package com.toxicrain.rainengine.core.eventbus.events.render.batchrenderer;

import com.toxicrain.rainengine.core.render.BatchRenderer;

public class RenderBatchRendererEvent {

    public final BatchRenderer batchRenderer;

    public RenderBatchRendererEvent(BatchRenderer batchRenderer) {
        this.batchRenderer = batchRenderer;
    }
}
