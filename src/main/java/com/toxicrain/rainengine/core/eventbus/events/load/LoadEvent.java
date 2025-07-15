package com.toxicrain.rainengine.core.eventbus.events.load;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoadEvent {

    public LoadEventStage loadEventStage;

    public enum LoadEventStage{
        PRE,
        ININT,
        POST,
        MANAGER
    }

}
