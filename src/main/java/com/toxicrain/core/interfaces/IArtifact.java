package com.toxicrain.core.interfaces;

import com.toxicrain.core.render.BatchRenderer;

/**
 * Interface representing an "artifact" in RainEngine.
 * This provides methods for updating and rendering artifacts.
 */
public interface IArtifact {

    /**
     * Updates the state of the artifact.
     * This method is intended to be overridden by implementing classes.
     * By default, it does nothing.
     */
    static void update() {
    }

    /**
     * Renders the artifact using the provided batch renderer.
     *
     * @param batchRenderer The renderer used to draw the artifact.
     */
    static void render(BatchRenderer batchRenderer) {
    }
}
