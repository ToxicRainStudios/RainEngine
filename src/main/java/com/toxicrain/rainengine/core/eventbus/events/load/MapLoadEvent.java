package com.toxicrain.rainengine.core.eventbus.events.load;

import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class MapLoadEvent {

    public final String mapName;
    public final int tileCount;
    public final Vector2 playerSpawnPos;

}
