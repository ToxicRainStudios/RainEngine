package com.toxicrain.artifacts;

public class Weapon {
    private String name;
    private int damage;
    private float range;
    private boolean isEquipped;

    public Weapon(String name, int damage, float range) {
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.isEquipped = false;
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
        } else {
            System.out.println("No weapon equipped.");
        }
    }
}