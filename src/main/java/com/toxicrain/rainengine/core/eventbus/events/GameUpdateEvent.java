package com.toxicrain.rainengine.core.eventbus.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameUpdateEvent {

    public final boolean gamePaused;
}
