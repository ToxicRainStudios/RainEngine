package com.toxicrain.rainengine.core.eventbus.events;

public class ArtifactUpdateEvent {

    public final String artifactName;

    public ArtifactUpdateEvent( String artifactName){
        this.artifactName = artifactName;
    }
}
