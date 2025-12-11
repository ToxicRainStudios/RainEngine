package com.toxicrain.rainengine.core.eventbus.events.lua;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecuteLuaScript {
    public final EventStage eventStage;

    public enum EventStage{
        ININT,
        POST_ININT,
        TICK,
        IMGUI
    }
}
