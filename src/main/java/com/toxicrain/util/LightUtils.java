package com.toxicrain.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LightUtils {
    @Getter
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
     * Removes a light source from the specified position with the specified strength.
     * @param x the x position of the light to be removed
     * @param y the y position of the light to be removed
     * @param strength the strength of the light to be removed
     * @return true if a light source was removed, false otherwise
     */
    public static boolean removeLightSource(float x, float y, float strength) {
        for (int i = 0; i < lightSources.size(); i++) {
            float[] lightSource = lightSources.get(i);
            if (lightSource[0] == x && lightSource[1] == y && lightSource[2] == strength) {
                lightSources.remove(i);
                return true;
            }
        }
        return false;
    }

}