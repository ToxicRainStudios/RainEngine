package com.toxicrain.rainengine.core.interfaces;

import com.toxicrain.rainengine.core.render.BatchRenderer;

/**
 * Interface representing an "artifact" in RainEngine.
 * This provides methods for updating and rendering artifacts.
 */
public interface IArtifact {

    /**
     * Updates the state of the artifact.
     * This method is intended to be overridden by implementing classes.
     */
    default void update(double deltaTime) {
    }

    /**
     * Renders the artifact using the provided batch renderer.
     * This method is intended to be overridden by implementing classes.
     *
     * @param batchRenderer The renderer used to draw the artifact.
     */
    default void render(BatchRenderer batchRenderer) {
    }
}
