package com.toxicrain.rainengine.core.eventbus.events.load.sound;

import com.toxicrain.rainengine.sound.SoundInfo;

public class SoundInfoLoadEvent {

    public final SoundInfo soundInfo;

    public SoundInfoLoadEvent(SoundInfo soundInfo){
        this.soundInfo = soundInfo;
    }
}
