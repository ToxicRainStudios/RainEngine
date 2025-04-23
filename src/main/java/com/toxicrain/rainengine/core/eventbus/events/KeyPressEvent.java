package com.toxicrain.rainengine.core.eventbus.events;

public class KeyPressEvent {
    public final int keyCode;
    public final int action; // GLFW_PRESS, GLFW_RELEASE, or GLFW_REPEAT

    public KeyPressEvent(int keyCode, int action) {
        this.keyCode = keyCode;
        this.action = action;
    }
}

