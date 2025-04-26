package com.toxicrain.rainengine.sound.music;

import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.sound.SoundInfo;
import com.toxicrain.rainengine.sound.SoundSystem;

import java.util.*;

public class MusicManager {

    private final List<String> TRACK_ORDER;
    private final Map<String, SoundInfo> SOUND_MAP;
    private final SoundSystem SOUND_SYSTEM;

    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    public MusicManager(Map<String, SoundInfo> sounds, SoundSystem soundSystem) {
        this.SOUND_MAP = sounds;
        this.SOUND_SYSTEM = soundSystem;
        this.TRACK_ORDER = new ArrayList<>(sounds.keySet());
    }

    /**
     * Start playing music
     */
    public void start() {
        if (!TRACK_ORDER.isEmpty() && !isPlaying) {
            playCurrentTrack();
        }
    }

    private void playCurrentTrack() {
        if (isPlaying || currentTrackIndex >= TRACK_ORDER.size()) return;

        String currentTrack = TRACK_ORDER.get(currentTrackIndex);
        SoundInfo info = SOUND_MAP.get(currentTrack);

        try {
            isPlaying = true;
            SOUND_SYSTEM.play(info, () -> {
                isPlaying = false;
                currentTrackIndex++;
                playCurrentTrack(); // Play next track immediately
            });

            RainLogger.RAIN_LOGGER.info("Now playing: {}", currentTrack);
        } catch (IllegalStateException e) {
            stop();
        }
    }

    public void stop() {
        isPlaying = false;
    }

    /**
     * Dynamically sets the next track to be played.
     * Inserts it into the trackOrder directly after the current one.
     *
     * @param trackName The name of the track (must exist in soundMap)
     */
    public void setNextTrack(String trackName) {
        if (!SOUND_MAP.containsKey(trackName)) {
            RainLogger.RAIN_LOGGER.warn("Tried to set unknown track as next: {}", trackName);
            return;
        }

        // Prevent duplicates by removing if it already exists in the list
        TRACK_ORDER.remove(trackName);

        // Insert right after the current track index
        int insertIndex = Math.min(currentTrackIndex + 1, TRACK_ORDER.size());
        TRACK_ORDER.add(insertIndex, trackName);

        RainLogger.RAIN_LOGGER.info("Inserted track '{}' to play next (after index {}).", trackName, currentTrackIndex);
    }

    /**
     * Sets the starting track that should play first.
     *
     * @param trackName The name of the track to start with (must exist in soundMap)
     */
    public void setStartingSound(String trackName) {
        if (!SOUND_MAP.containsKey(trackName)) {
            RainLogger.RAIN_LOGGER.warn("Tried to set unknown track as starting sound: {}", trackName);
            return;
        }

        TRACK_ORDER.remove(trackName); // Avoid duplicate
        TRACK_ORDER.add(0, trackName); // Insert at the beginning
        currentTrackIndex = 0;        // Reset index to start with this track

        RainLogger.RAIN_LOGGER.info("Set starting track to '{}'", trackName);
    }

    /**
     * Get the currently playing track name.
     *
     * @return The name of the track that is currently playing or null if no track is playing.
     */
    public String getCurrentTrackName() {
        if (currentTrackIndex >= 0 && currentTrackIndex < TRACK_ORDER.size()) {
            return TRACK_ORDER.get(currentTrackIndex);
        }
        return null; // Return null if no track is currently playing.
    }

}
