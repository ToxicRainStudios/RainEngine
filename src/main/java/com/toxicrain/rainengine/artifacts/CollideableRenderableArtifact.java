package com.toxicrain.rainengine.artifacts;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.eventbus.events.ArtifactUpdateEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import lombok.Getter;

public class CollideableRenderableArtifact extends RenderableArtifact {

    @Getter
    private final AABB aabb;

    public CollideableRenderableArtifact(Resource textureResource, float x, float y, float rotation, AABB aabb) {
        super(textureResource, x, y, rotation);
        this.aabb = aabb;

        SmeagleBus.getInstance().listen(ArtifactUpdateEvent.class)
                .subscribe(event -> {
                    if (event.artifact instanceof CollideableRenderableArtifact){ //TODO ofc none of this works but heres how it would go
                        if(handleCollisions((float) DeltaTimeUtil.getDeltaTime())){
                            RainLogger.RAIN_LOGGER.info("Name: " + event.artifact.getClass());
                            event.setCanceled(true); //Artifact going to collide, cancel event to prevent movement
                        }

                    }

                });
    }

    private boolean handleCollisions(float deltaTime){
        return false;
    }
}
