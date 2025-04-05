package com.toxicrain.sound.music;

import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;
import java.util.Map;

public class MusicManager {

    private Map<String, SoundInfo> soundMap;
    private SoundSystem soundSystem;
    private String currentState = "CALM";

    public MusicManager(Map<String, SoundInfo> sounds, SoundSystem soundSystem) {
        this.soundMap = sounds;
        this.soundSystem = soundSystem;
    }

    /**
     * Updates the music system to use a new sound.
     *
     * @param soundClip The music clip to use. Must in {@link MusicManager#soundMap}
     */
    public void update(String soundClip) {
        if (!soundClip.equals(currentState)) {
            // Check if the game state exists in the map
            if (soundMap.containsKey(soundClip)) {
                soundSystem.play(soundMap.get(soundClip));
            } else {
                throw new RuntimeException(soundClip + " is not a valid music clip!");
            }
            currentState = soundClip;
        }
    }
}
