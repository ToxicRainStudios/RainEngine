package com.toxicrain.rainengine.core.eventbus.events;

import com.toxicrain.rainengine.artifacts.Weapon;

public class WeaponRegisterEvent {

    public final Weapon weapon;

    public WeaponRegisterEvent(Weapon weapon) {
        this.weapon = weapon;
    }

}
