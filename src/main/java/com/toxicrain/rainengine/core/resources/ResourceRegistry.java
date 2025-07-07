package com.toxicrain.rainengine.core.resources;

import com.toxicrain.rainengine.core.datatypes.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ResourceRegistry<T> {

    private final Function<String, T> loader;
    private final Map<Resource, T> resources = new HashMap<>();
    private final Map<Resource, String> filePaths = new HashMap<>();

    public ResourceRegistry(Function<String, T> loader) {
        this.loader = loader;
    }

    public void load(Resource location, String filePath) {
        T resource = loader.apply(filePath);
        resources.put(location, resource);
        filePaths.put(location, filePath);
    }

    public T get(Resource location) {
        return resources.get(location);
    }

    public void reload() {
        for (Map.Entry<Resource, String> entry : filePaths.entrySet()) {
            Resource location = entry.getKey();
            String filePath = entry.getValue();
            T resource = loader.apply(filePath);
            resources.put(location, resource);
        }
    }

    public void clear() {
        resources.clear();
        filePaths.clear();
    }
}
