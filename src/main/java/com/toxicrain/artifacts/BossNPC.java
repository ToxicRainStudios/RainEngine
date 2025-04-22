package com.toxicrain.artifacts;

import com.toxicrain.core.Color;
import com.toxicrain.core.Constants;
import com.toxicrain.core.RainLogger;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.texture.TextureSystem;

public class BossNPC extends NPC {

    private float maxHealth;
    private float currentHealth;
    private int currentPhase;
    private float attackCooldown;
    private float attackTimer;
    private String bossTexture;

    public BossNPC(float startingXpos, float startingYpos, float rotation, float size, float maxHealth) {
        super(startingXpos, startingYpos, rotation, size ); // Boss is bigger than a regular NPC

        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.currentPhase = 1;
        this.attackCooldown = 2.0f; // 2 seconds between attacks
        this.attackTimer = 0f;
        this.bossTexture = "spiderTexture"; // Boss has its own texture

        // Assign a boss-specific behavior sequence
        GameFactory.npcManager.addNPC(this, this.behaviorSequence);

        RainLogger.RAIN_LOGGER.info("Boss NPC spawned at: ({}, {})", startingXpos, startingYpos);
    }

    public void takeDamage(float damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            die();
        } else {
            checkPhaseChange();
        }
    }

    private void checkPhaseChange() {
        if (currentHealth <= maxHealth * 0.5f && currentPhase == 1) {
            enterPhaseTwo();
        }
    }

    private void enterPhaseTwo() {
        currentPhase = 2;
        attackCooldown = 1.0f; // Faster attacks
        System.out.println("Boss entered Phase Two!");
        // You could change textures, effects, abilities, etc.
    }

    private void die() {
        System.out.println("Boss defeated!");
        // Remove boss from NPC manager, spawn loot, trigger events, etc.
        GameFactory.npcManager.removeNPC(this);
    }

    // Override to add special attacks or behavior
    @Override
    public void moveTowardsPlayer(float speed) {
        // Boss moves slower in phase 1, faster in phase 2
        if (currentPhase == 1) {
            super.moveTowardsPlayer(speed * 0.5f);
        } else {
            super.moveTowardsPlayer(speed * 1.2f);
        }

        this.handleCollisions();
    }

    public void update(float deltaTime) {
        attackTimer += deltaTime;

        if (canSeePlayer()) {
            moveTowardsPlayer(0.05f);

            if (attackTimer >= attackCooldown) {
                performAttack();
                attackTimer = 0;
            }
        }
    }

    private void performAttack() {
        if (currentPhase == 1) {
            basicAttack();
        } else if (currentPhase == 2) {
            areaAttack();
        }
    }

    private void basicAttack() {
        System.out.println("Boss performs a basic attack!");
        // Logic for dealing damage to the player (e.g., melee attack)
    }

    private void areaAttack() {
        System.out.println("Boss performs a powerful area attack!");
        // You can spawn projectiles, deal area damage, etc.
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        // Boss-specific render
        batchRenderer.addTexture(TextureSystem.getTexture(bossTexture), this.X, this.Y, Constants.npcZLevel+0.1f,
                this.rotation, 2f, 2f, Color.toFloatArray(Color.WHITE));

        renderHealthBar(batchRenderer);
    }

    private void renderHealthBar(BatchRenderer batchRenderer) {
        float healthBarWidth = 100f;
        float healthBarHeight = 10f;
        float healthRatio = currentHealth / maxHealth;
    }
}

