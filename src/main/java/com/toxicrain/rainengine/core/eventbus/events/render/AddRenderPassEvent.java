package com.toxicrain.rainengine.core.eventbus.events.render;

import com.toxicrain.rainengine.core.render.rendering.Renderer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddRenderPassEvent {
    public Renderer renderer;
}
