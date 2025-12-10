package com.toxicrain.rainengine.core.eventbus.events;

import com.toxicrain.rainengine.artifacts.Camera;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameUpdateEvent {

    public final boolean gamePaused;
    public final Camera camera;
}
