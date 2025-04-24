package com.toxicrain.rainengine.core.eventbus.events.load.sound;

import com.toxicrain.rainengine.sound.SoundSystem;

public class SoundSystemLoadEvent {

    public final SoundSystem soundSystem;

    public SoundSystemLoadEvent(SoundSystem soundSystem){
        this.soundSystem = soundSystem;
    }
}
