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
}
