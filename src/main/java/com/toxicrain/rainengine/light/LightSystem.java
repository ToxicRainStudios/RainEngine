package com.toxicrain.rainengine.light;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class LightSystem {
    @Getter
    private static final List<float[]> LIGHT_SOURCES = new ArrayList<>();

    /**
     * Adds a light source to a position with a strength
     * @param x the x position for the light
     * @param y the y position for the light
     * @param strength the strength of the light, cannot be 0 or 1
     */
    public static void addLightSource(float x, float y, float strength) {
        LIGHT_SOURCES.add(new float[] { x, y, strength });
    }

    /**
     * Removes a light source from the specified position with the specified strength.
     * @param x the x position of the light to be removed
     * @param y the y position of the light to be removed
     * @param strength the strength of the light to be removed
     * @return true if a light source was removed, false otherwise
     */
    public static boolean removeLightSource(float x, float y, float strength) {
        for (int i = 0; i < LIGHT_SOURCES.size(); i++) {
            float[] lightSource = LIGHT_SOURCES.get(i);
            if (lightSource[0] == x && lightSource[1] == y && lightSource[2] == strength) {
                LIGHT_SOURCES.remove(i);
                return true;
            }
        }
        return false;
    }

}