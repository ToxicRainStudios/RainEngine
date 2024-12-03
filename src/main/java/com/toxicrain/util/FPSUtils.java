package com.toxicrain.util;

import com.toxicrain.core.RainLogger;
import org.lwjgl.glfw.GLFW;


public class FPSUtils {
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
            RainLogger.printLOG("FPS: " + fps);

            // Reset for next second
            frameCount = 0;
            elapsedTime = 0;
        }
    }
}