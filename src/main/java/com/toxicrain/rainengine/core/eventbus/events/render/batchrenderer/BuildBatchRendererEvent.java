package com.toxicrain.rainengine.core.eventbus.events.render.batchrenderer;

import com.toxicrain.rainengine.core.render.BatchRenderer;

public class BuildBatchRendererEvent {

    public final BatchRenderer batchRenderer;

    public BuildBatchRendererEvent(BatchRenderer batchRenderer) {
        this.batchRenderer = batchRenderer;
    }
}
