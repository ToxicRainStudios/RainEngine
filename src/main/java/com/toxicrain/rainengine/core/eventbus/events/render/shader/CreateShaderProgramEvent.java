package com.toxicrain.rainengine.core.eventbus.events.render.shader;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateShaderProgramEvent {
    public final String name;
    public final String vertexShaderPath;
    public final String fragmentShaderPath;
}
