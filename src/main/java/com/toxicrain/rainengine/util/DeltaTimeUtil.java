package com.toxicrain.rainengine.util;

public class DeltaTimeUtil {

    private static long lastFrameTime = System.nanoTime();
    private static float deltaTime = 0f;

    /**
     * Call this once per frame to update the delta time.
     */
    public static void update() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
        lastFrameTime = currentTime;
    }

    /**
     * Get the time (in seconds) since the last frame.
     *
     * @return delta time in seconds
     */
    public static float getDeltaTime() {
        return deltaTime;
    }

    /**
     * Resets the timer manually (optional use).
     */
    public static void reset() {
        lastFrameTime = System.nanoTime();
        deltaTime = 0f;
    }
}

