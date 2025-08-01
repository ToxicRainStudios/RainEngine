package com.toxicrain.rainengine.core.datatypes;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Getter
public class Resource {

    private final String namespace;
    private final String path;

    public Resource(String namespace, String path) {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        this.namespace = namespace.toLowerCase();
        this.path = path.toLowerCase();
    }

    public Resource(String location) {
        String[] parts = location.split(":", 2);
        if (parts.length == 2) {
            this.namespace = parts[0].toLowerCase();
            this.path = parts[1].toLowerCase();
        } else {
            this.namespace = "rainengine"; // Default namespace if none provided
            this.path = location.toLowerCase();
        }
    }

    /**
     * Compute a Resource from a base directory and file path.
     */
    public static Resource fromFile(String textureDirectory, Path path) {
        Path relativePath = Paths.get(textureDirectory).relativize(path);
        String[] pathParts = relativePath.toString().replace("\\", "/").split("/");

        String namespace;
        String resourcePath;

        if (pathParts.length >= 2) {
            namespace = pathParts[0];
            resourcePath = String.join("/", pathParts).substring(namespace.length() + 1)
                    .replaceFirst("[.][^.]+$", ""); // Remove file extension
        } else {
            namespace = "rainengine";
            resourcePath = relativePath.toString().replace("\\", "/")
                    .replaceFirst("[.][^.]+$", ""); // Remove file extension
        }

        return new Resource(namespace, resourcePath);
    }

    /**
     * Returns the full string representation like "namespace:path"
     */
    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    /**
     * Converts to a filesystem path: "namespace/path"
     */
    public String toFilePath() {
        return namespace + "/" + path;
    }

    /**
     * Equality based on namespace and path
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource that = (Resource) o;
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}
