package com.toxicrain.rainengine.core.registries.manager;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.artifacts.projectile.Projectile;
import com.toxicrain.rainengine.artifacts.IArtifact;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactManager extends BaseInstanceable<ArtifactManager> {

    private static final int MAX_PROJECTILES = 500;

    private final List<IArtifact> artifacts;

    public static ArtifactManager getInstance() {
        return BaseInstanceable.getInstance(ArtifactManager.class);
    }

    private ArtifactManager() {
        artifacts = new ArrayList<>();
    }

    public void addArtifact(IArtifact artifact) {

        // Special rule for projectiles
        if (artifact instanceof Projectile) {
            long projectileCount = artifacts.stream()
                    .filter(a -> a instanceof Projectile)
                    .count();

            if (projectileCount >= MAX_PROJECTILES) {
                removeOldestProjectile();
            }
        }

        artifacts.add(artifact);
    }

    public void update(double deltaTime) {
        Iterator<IArtifact> iterator = artifacts.iterator();

        while (iterator.hasNext()) {
            IArtifact artifact = iterator.next();
            artifact.update(deltaTime);

            // Specialized removal logic
            if (artifact instanceof Projectile) {
                Projectile projectile = (Projectile) artifact;

                if (projectile.getLifeTime() > 10.0f) {
                    RainLogger.RAIN_LOGGER.debug("Removing projectile: {}", projectile);
                    iterator.remove();
                }
            }
        }
    }

    public void render(BatchRenderer batchRenderer) {
        for (IArtifact artifact : artifacts) {
            artifact.render(batchRenderer);
        }
    }

    public void clearAll() {
        RainLogger.RAIN_LOGGER.debug("Clearing all artifacts");
        artifacts.clear();
    }

    public void removeArtifact(IArtifact artifact) {
        artifacts.remove(artifact);
    }

    private void removeOldestProjectile() {
        for (Iterator<IArtifact> it = artifacts.iterator(); it.hasNext();) {
            IArtifact artifact = it.next();
            if (artifact instanceof Projectile) {
                it.remove();
                return;
            }
        }
    }

    public <T extends IArtifact> List<T> getArtifactsByType(Class<T> clazz) {
        return artifacts.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    public List<IArtifact> getAllArtifacts() {
        return artifacts;
    }
}