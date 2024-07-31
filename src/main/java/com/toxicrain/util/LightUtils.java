package com.toxicrain.util;

import java.util.ArrayList;
import java.util.List;

public class LightUtils {
    private static final List<float[]> lightSources = new ArrayList<>();

    /**
     * Adds a light source to a position with a strength
     * @param x the x position for the light
     * @param y the y position for the light
     * @param strength the strength of the light, cannot be 0 or 1
     */
    public static void addLightSource(float x, float y, float strength) {
        lightSources.add(new float[] { x, y, strength });
    }


    /**
     * Get the list of light sources and their positions
     *
     * @return {@link LightUtils#lightSources}
     */
    public static List<float[]> getLightSources() {

        return lightSources;
    }

}