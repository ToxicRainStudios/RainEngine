package com.toxicrain.artifacts;

import com.toxicrain.util.MathUtils;

public class Weapon {
    private String name;
    private int damage;
    private float range;
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

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public float getRange() {
        return range;
    }

    public boolean isEquipped() {
        return isEquipped;
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