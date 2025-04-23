package com.toxicrain.rainengine.core;

import lombok.NonNull;

import java.lang.reflect.Method;

public class GameLoader {

    public static void loadAndInitGame(@NonNull String className) {
        try {
            // Load the class
            Class<?> clazz = Class.forName(className);

            // Find and invoke the static init() method
            Method initMethod = clazz.getMethod("init");
            initMethod.invoke(null); // null because it's a static method

            RainLogger.RAIN_LOGGER.info("Game initialized from: {}", className);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("No init() method found in: " + className);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to invoke init() on: " + className);
            e.printStackTrace();
        }
    }
}
