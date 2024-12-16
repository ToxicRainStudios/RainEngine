package com.toxicrain.artifacts;

import com.toxicrain.texture.TextureInfo;
import com.toxicrain.util.MathUtils;
import lombok.Getter;

public class Weapon {
    @Getter
    private String name;
    @Getter
    private int damage;
    @Getter
    private float range;
    @Getter
    private boolean isEquipped;
    private int maxShot;
    private int minShot;
    private TextureInfo projectileTexture;

    public Weapon(String name, int damage, float range, int maxShot, int minShot, TextureInfo projectileTexture) {
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.isEquipped = false;
        this.maxShot = maxShot;
        this.minShot = minShot;
        this.projectileTexture = projectileTexture;
    }

    public void equip() {
        this.isEquipped = true;
    }

    public void unequip() {
        this.isEquipped = false;
    }

    public void attack(float playerAngle, float playerPosX, float playerPosY) {
        if (isEquipped) {
            System.out.println("Attacking with " + name + " for " + damage + " damage!");

            // Get a random number of projectiles to fire based on the weapon's shot range
            int shotsToFire = MathUtils.getRandomIntBetween(minShot, maxShot);
            System.out.println("Firing " + shotsToFire + " projectiles!");

            // Fire the projectiles
            for (int i = 0; i < shotsToFire; i++) {
                // Adjust the projectile's position slightly for each shot (e.g., randomize x position)
                float xpos = playerPosX + (i * 2f);  // Example: Slightly offset each shot
                float ypos = playerPosY;  // Start from player's position
                createProjectile(xpos, ypos, playerAngle);  // Use player angle for projectile direction
            }
        } else {
            System.out.println("No weapon equipped.");
        }
    }

    private void createProjectile(float xpos, float ypos, float playerAngle) {
        // Calculate velocity based on the player's angle
        float velocityX = (float) Math.cos(playerAngle) * range;  // Adjust based on angle
        float velocityY = (float) Math.sin(playerAngle) * range;  // Adjust based on angle

        // Create the projectile at the player's position, moving in the direction of the angle
        new Projectile(xpos, ypos, velocityX, velocityY, projectileTexture);

        System.out.println("Projectile fired from " + name + "!");
    }
}
