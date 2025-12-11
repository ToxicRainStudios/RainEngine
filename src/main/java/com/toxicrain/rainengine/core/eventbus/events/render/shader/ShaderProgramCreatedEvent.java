package com.toxicrain.rainengine.core.eventbus.events.render.shader;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShaderProgramCreatedEvent {
    public final String name;
    public final int program;

}
