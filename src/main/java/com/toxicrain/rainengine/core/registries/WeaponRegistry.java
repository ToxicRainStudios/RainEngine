package com.toxicrain.rainengine.core.registries;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.artifacts.Weapon;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.eventbus.events.WeaponRegisterEvent;
import com.toxicrain.rainengine.factories.GameFactory;

import java.util.HashMap;
import java.util.Map;

public class WeaponRegistry {
    private static final Map<String, Weapon> WEAPONS = new HashMap<>();

    public static void register(Weapon weapon) {
        if (WEAPONS.containsKey(weapon.getName())) {
            throw new IllegalArgumentException("Weapon '" + weapon.getName() + "' is already registered!");
        }
        SmeagleBus.getInstance().post(new WeaponRegisterEvent(weapon));

        RainLogger.RAIN_LOGGER.info("Registering: {}", weapon.getName());
        WEAPONS.put(weapon.getName(), weapon);
    }

    public static Weapon get(String name) {
        if (!WEAPONS.containsKey(name)) {
            throw new IllegalArgumentException("Weapon " + name + " does not exist!");
        }

        return WEAPONS.get(name);
    }

    public static boolean contains(String name) {
        return WEAPONS.containsKey(name);
    }

    public static Map<String, Weapon> getAllWeapons() {
        return Map.copyOf(WEAPONS); // immutable copy
    }
}

