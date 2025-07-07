package com.toxicrain.rainengine.core.eventbus.events;

public class ScrollEvent {

    public final float yOffeset;
    public final float xOffset;


    public ScrollEvent(float xOffset, float yOffeset) {
        this.xOffset = xOffset;
        this.yOffeset = yOffeset;
    }

}
