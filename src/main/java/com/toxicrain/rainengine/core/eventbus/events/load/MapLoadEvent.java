package com.toxicrain.rainengine.core.eventbus.events.load;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.joml.Vector2f;

@AllArgsConstructor
@ToString
public class MapLoadEvent {

    public final String mapName;
    public final int tileCount;
    public final Vector2f playerSpawnPos;

}
