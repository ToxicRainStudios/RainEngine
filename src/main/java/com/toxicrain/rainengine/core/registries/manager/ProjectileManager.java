package com.toxicrain.rainengine.core.registries.manager;

import com.toxicrain.rainengine.artifacts.Projectile;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.render.BatchRenderer;

import java.util.ArrayList;
import java.util.List;

public class ProjectileManager {

    private static final int MAX_PROJECTILES = 500; // Maximum number of projectiles allowed
    private final List<Projectile> projectiles;

    public ProjectileManager() {
        projectiles = new ArrayList<>();
    }

    // Add a new projectile to the manager
    public void addProjectile(Projectile projectile) {
        // If the list exceeds the maximum size, remove the oldest projectile
        while (projectiles.size() >= MAX_PROJECTILES) {
            projectiles.remove(0); // Remove the first projectile (oldest)
        }
        projectiles.add(projectile); // Add the new projectile
    }

    // Update all projectiles
    public void update(double deltaTime) {
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);

            // Update the projectile's position and age
            projectile.update();

            // Remove projectiles that meet removal criteria
            if (shouldRemove(projectile)) {
                projectiles.remove(i);
                i--; // Adjust index after removal
            }
        }
    }

    // Render all projectiles
    public void render(BatchRenderer batchRenderer) {
        for (Projectile projectile : projectiles) {
            projectile.render(batchRenderer);
        }
    }

    // Get all projectiles (if needed)
    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    // Remove a specific projectile
    public void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
    }

    // A method to check if a projectile should be removed
    private boolean shouldRemove(Projectile projectile) {
        if (projectile.getLifeTime() > 10.0f) {
            RainLogger.RAIN_LOGGER.debug("Removing projectile: {}", projectile);
            return true;
        }
        return false;
    }
}
