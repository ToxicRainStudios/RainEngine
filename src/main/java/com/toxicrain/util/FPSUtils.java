package com.toxicrain.util;

import com.toxicrain.core.Logger;
import org.lwjgl.glfw.GLFW;


public class FPSUtils {
    private static final long NANOS_IN_SECOND = 1_000_000_000;
    private int frameCount = 0;
    private double lastTime = GLFW.glfwGetTime();
    private double elapsedTime = 0;

    public void update() {
        double currentTime = GLFW.glfwGetTime();
        frameCount++;
        elapsedTime += (currentTime - lastTime);
        lastTime = currentTime;

        if (elapsedTime >= 1.0) {
            // Calculate FPS
            int fps = frameCount;
            Logger.printLOG("FPS: " + fps);

            // Reset for next second
            frameCount = 0;
            elapsedTime -= 1.0;
        }
    }
}