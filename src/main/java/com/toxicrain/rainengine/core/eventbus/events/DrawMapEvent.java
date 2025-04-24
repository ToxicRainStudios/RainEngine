package com.toxicrain.rainengine.core.eventbus.events;

import com.toxicrain.rainengine.core.render.BatchRenderer;
import lombok.Getter;

public class DrawMapEvent {

    @Getter
    private final BatchRenderer batchRenderer;

    public DrawMapEvent(BatchRenderer batchRenderer) {
        this.batchRenderer = batchRenderer;
    }
}
