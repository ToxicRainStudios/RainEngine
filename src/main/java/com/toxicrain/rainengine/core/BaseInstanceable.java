package com.toxicrain.rainengine.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Fully automated base class to provide singleton support to subclasses.
 * No need to declare an instance field in the subclass.
 * @param <T> The subclass type.
 */
public abstract class BaseInstanceable<T extends BaseInstanceable<T>> {

    private static final Map<Class<?>, Object> INSTANCES = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static <T extends BaseInstanceable<T>> T getInstance(Class<T> clazz) {
        synchronized (INSTANCES) {
            if (!INSTANCES.containsKey(clazz)) {
                try {
                    Constructor<T> constructor = clazz.getDeclaredConstructor();

                    // Force access to private/protected constructors
                    constructor.setAccessible(true);

                    T instance = constructor.newInstance();
                    INSTANCES.put(clazz, instance);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create instance for class: " + clazz.getName(), e);
                }
            }
            return (T) INSTANCES.get(clazz);
        }
    }
}
