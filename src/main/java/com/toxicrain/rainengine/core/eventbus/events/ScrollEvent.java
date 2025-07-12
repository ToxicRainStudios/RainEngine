package com.toxicrain.rainengine.core.eventbus.events;

public class ScrollEvent {

    public final float yOffset;
    public final float xOffset;


    public ScrollEvent(float xOffset, float yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

}
