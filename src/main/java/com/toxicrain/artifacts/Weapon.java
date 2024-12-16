package com.toxicrain.artifacts;

import com.toxicrain.core.RainLogger;
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
    private long lastAttackTime; // Tracks the last time the weapon was used
    private long cooldown; // Cooldown duration in milliseconds
    private float spread;

    public Weapon(String name, int damage, float range, int maxShot, int minShot, TextureInfo projectileTexture, long cooldown, float spread) {
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.isEquipped = false;
        this.maxShot = maxShot;
        this.minShot = minShot;
        this.projectileTexture = projectileTexture;
        this.cooldown = cooldown;
        this.lastAttackTime = 0;
        this.spread = spread;
    }

    public void equip() {
        this.isEquipped = true;
    }

    public void unequip() {
        this.isEquipped = false;
    }

    public void attack(float playerAngle, float playerPosX, float playerPosY) {
        if (isEquipped) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime < cooldown) {
                RainLogger.rainLogger.debug("Weapon is on cooldown. Wait " + (cooldown - (currentTime - lastAttackTime)) + " ms.");
                return;
            }

            lastAttackTime = currentTime; // Update the last attack time
            RainLogger.rainLogger.debug("Attacking with " + name + " for " + damage + " damage!");

            // Get a random number of projectiles to fire based on the weapon's shot range
            int shotsToFire = MathUtils.getRandomIntBetween(minShot, maxShot);
            RainLogger.rainLogger.debug("Firing " + shotsToFire + " projectiles!");

            // Fire the projectiles with randomness in position and angle
            for (int i = 0; i < shotsToFire; i++) {

                // Add slight random variation to the angle
                float angleVariation = MathUtils.getRandomFloatBetween(-spread, spread); // Angle in degrees
                float randomizedAngle = playerAngle + angleVariation;

                createProjectile(playerPosX, playerPosY, randomizedAngle); // Use randomized angle and position
            }
        } else {
            RainLogger.rainLogger.debug("No weapon equipped.");
        }
    }

    private void createProjectile(float xpos, float ypos, float playerAngle) {
        // Rotate the angle by 90 degrees and convert to radians
        float angleInRadians = (float) Math.toRadians(playerAngle + 90);

        // Calculate velocity from the adjusted angle
        float velocityX = (float) Math.cos(angleInRadians) * 0.001f; // Adjust speed as needed
        float velocityY = (float) Math.sin(angleInRadians) * 0.001f;

        // Spawn the projectile
        new Projectile(xpos, ypos, velocityX, velocityY, projectileTexture);

        System.out.println("Projectile created at (" + xpos + ", " + ypos + ") with velocity (" + velocityX + ", " + velocityY + ")");
    }
}
