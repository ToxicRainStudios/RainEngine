package com.toxicrain.util;

import java.util.Random;

public class MathUtils {

    private static final Random random = new Random();

    public static int getRandomIntBetween(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than Max");
        }
        if (min == max) { //It's stupid to do math if they are the same
            return min;
        }
        return random.nextInt((max - min) + 1) + min;
    }

    public static float getRandomFloatBetween(float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than Max");
        }
        if (min == max) { //It's stupid to do math if they are the same
            return min;
        }
        return random.nextFloat((max - min) + 1) + min;
    }

    public static float clamp(float value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Calculates the angle in degrees between the positive x-axis and the point (x, y).
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The angle in degrees.
     */
    public static float calculateAngle(double x, double y) {
        // Calculate the angle in radians using Math.atan2
        double angleInRadians = Math.atan2(y, x);

        // Convert radians to degrees
        double angleInDegrees = Math.toDegrees(angleInRadians);

        return (float) angleInDegrees;
    }

    public static boolean approximatelyEqual(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon;
    }
}
