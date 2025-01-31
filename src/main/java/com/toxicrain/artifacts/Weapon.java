package com.toxicrain.artifacts;

import com.toxicrain.core.RainLogger;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;
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
    private SoundInfo soundInfo;

    public Weapon(String name, int damage, float range, int maxShot, int minShot, TextureInfo projectileTexture, long cooldown, float spread, String soundInfo) {
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
        this.soundInfo = SoundSystem.getSound(soundInfo);
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

            // Play weapon sound
            GameFactory.soundSystem.play(this.soundInfo);

            lastAttackTime = currentTime; // Update last attack time
            RainLogger.rainLogger.debug("Attacking with " + name + " for " + damage + " damage!");

            // Get a random number of projectiles to fire based on the weapon's shot range
            int shotsToFire = MathUtils.getRandomIntBetween(minShot, maxShot);
            RainLogger.rainLogger.debug("Firing " + shotsToFire + " projectiles!");

            for (int i = 0; i < shotsToFire; i++) {
                // Add slight random variation to the angle (converted to radians if needed)
                float angleVariation = (float) Math.toRadians(MathUtils.getRandomFloatBetween(-spread, spread));
                float randomizedAngle = playerAngle + angleVariation;

                RainLogger.rainLogger.debug("Projectile " + (i + 1) + " fired at angle: " + Math.toDegrees(randomizedAngle) + " degrees.");

                createProjectile(playerPosX, playerPosY, randomizedAngle);
            }
        } else {
            RainLogger.rainLogger.debug("No weapon equipped.");
        }
    }

    private void createProjectile(float xpos, float ypos, float playerAngle) {
        // Ensure playerAngle is in radians (it should be already if from atan2)

        float speed = 0.1f; // Adjust speed as needed

        // Calculate velocity from the playerAngle
        float velocityX = (float) Math.cos(playerAngle) * speed;
        float velocityY = (float) Math.sin(playerAngle) * speed;

        // Spawn the projectile
        new Projectile(xpos, ypos, velocityX, velocityY, projectileTexture);

        System.out.println("Projectile created at (" + xpos + ", " + ypos +
                ") with velocity (" + velocityX + ", " + velocityY + ") and angle: " +
                Math.toDegrees(playerAngle) + " degrees");
    }
}
