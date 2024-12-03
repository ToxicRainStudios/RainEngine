package com.toxicrain.artifacts.manager;

import com.toxicrain.artifacts.Projectile;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;

import java.util.ArrayList;
import java.util.List;

public class ProjectileManager {

    private final List<Projectile> projectiles;

    public ProjectileManager() {
        projectiles = new ArrayList<>();
    }

    // Add a new projectile to the manager
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    // Update all projectiles
    public void update(float deltaTime) {
        // Loop through all projectiles and update their position
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            projectile.update();  // Update the projectile's position based on velocity

            // Optionally remove projectiles if they are out of bounds, or if they should expire
            if (shouldRemove(projectile)) {
                projectiles.remove(i);
                i--;  // Adjust index after removal to avoid skipping elements
            }
        }
    }

    // Render all projectiles
    public void render(BatchRenderer batchRenderer) {
        for (Projectile projectile : projectiles) {
            projectile.render(batchRenderer);  // Render each projectile
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
        //TODO finish me
        return false;
    }
}
