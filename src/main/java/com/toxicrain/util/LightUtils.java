package com.toxicrain.util;

import java.util.ArrayList;
import java.util.List;

public class LightUtils {
    private static List<float[]> lightSources = new ArrayList<>();

    public static void addLightSource(float x, float y, float maxDistance) {
        lightSources.add(new float[] { x, y, maxDistance });
    }

    public static List<float[]> getLightSources() {

        return lightSources;
    }

}