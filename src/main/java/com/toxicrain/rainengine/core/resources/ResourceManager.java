package com.toxicrain.rainengine.core.resources;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ResourceManager {

    private static final Map<Class<?>, ResourceRegistry<?>> registries = new HashMap<>();

    /**
     * Registers a resource type with a loading function.
     */
    public static <T> void register(Class<T> type, Function<String, T> loader) {
        registries.put(type, new ResourceRegistry<>(loader));
    }

    /**
     * Loads a resource into the registry.
     */
    public static <T> void load(Class<T> type, Resource location, String filePath) {
        @SuppressWarnings("unchecked")
        ResourceRegistry<T> registry = (ResourceRegistry<T>) registries.get(type);
        if (registry == null) {
            throw new IllegalStateException("Resource type not registered: " + type.getSimpleName());
        }

        registry.load(location, filePath);
        RainLogger.RAIN_LOGGER.info("Loaded {}: {}", type.getSimpleName(), location);
    }

    /**
     * Retrieves a loaded resource.
     */
    public static <T> T get(Class<T> type, Resource location) {
        @SuppressWarnings("unchecked")
        ResourceRegistry<T> registry = (ResourceRegistry<T>) registries.get(type);
        if (registry == null) {
            throw new IllegalStateException("Resource type not registered: " + type.getSimpleName());
        }

        return registry.get(location);
    }

    /**
     * Reloads all resources across all registries.
     */
    public static void reloadAll() {
        RainLogger.RAIN_LOGGER.info("Reloading all resources...");
        for (Map.Entry<Class<?>, ResourceRegistry<?>> entry : registries.entrySet()) {
            entry.getValue().reload();
            RainLogger.RAIN_LOGGER.info("Reloaded resources of type: {}", entry.getKey().getSimpleName());
        }
        RainLogger.RAIN_LOGGER.info("Resource reload complete.");
    }

    /**
     * Reloads resources for a specific type.
     */
    public static <T> void reload(Class<T> type) {
        @SuppressWarnings("unchecked")
        ResourceRegistry<T> registry = (ResourceRegistry<T>) registries.get(type);
        if (registry == null) {
            throw new IllegalStateException("Resource type not registered: " + type.getSimpleName());
        }

        RainLogger.RAIN_LOGGER.info("Reloading resources of type: {}", type.getSimpleName());
        registry.reload();
        RainLogger.RAIN_LOGGER.info("Reload complete for type: {}", type.getSimpleName());
    }

    /**
     * Clears all registries (optional, if you want a full unload).
     */
    public static void clearAll() {
        for (ResourceRegistry<?> registry : registries.values()) {
            registry.clear();
        }
        RainLogger.RAIN_LOGGER.info("Cleared all registered resources.");
    }
}
