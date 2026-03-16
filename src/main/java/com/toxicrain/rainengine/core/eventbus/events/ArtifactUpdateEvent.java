package com.toxicrain.rainengine.core.eventbus.events;

import com.github.strubium.smeaglebus.eventbus.CancelableEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArtifactUpdateEvent extends CancelableEvent {

    public final Object artifact;
}
