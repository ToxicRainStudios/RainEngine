package com.toxicrain.core.registries;

import com.toxicrain.artifacts.Weapon;
import com.toxicrain.core.RainLogger;

import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry {
    private static final Map<String, Weapon> WEAPONS = new HashMap<>();

    public static void register(Weapon weapon) {
        if (WEAPONS.containsKey(weapon.getName())) {
            throw new IllegalArgumentException("Weapon '" + weapon.getName() + "' is already registered!");
        }
        RainLogger.RAIN_LOGGER.info("Registering: " + weapon.getName());
        WEAPONS.put(weapon.getName(), weapon);
    }

    public static Weapon get(String name) {
        return WEAPONS.get(name);
    }

    public static boolean contains(String name) {
        return WEAPONS.containsKey(name);
    }

    public static Map<String, Weapon> getAllWeapons() {
        return Map.copyOf(WEAPONS); // immutable copy
    }
}

