package com.toxicrain.artifacts;

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

    public Weapon(String name, int damage, float range, int maxShot, int minShot) {
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.isEquipped = false;
        this.maxShot = maxShot;
        this.minShot = minShot;
    }

    public void equip() {
        this.isEquipped = true;
    }

    public void unequip() {
        this.isEquipped = false;
    }

    public void attack() {
        if (isEquipped) {
            System.out.println("Attacking with " + name + " for " + damage + " damage!");
            System.out.println(MathUtils.getRandomIntBetween(minShot, maxShot));
        } else {
            System.out.println("No weapon equipped.");
        }
    }
}