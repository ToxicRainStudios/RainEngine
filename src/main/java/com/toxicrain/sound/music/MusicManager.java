package com.toxicrain.sound.music;

import com.toxicrain.core.RainLogger;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;

import java.util.*;

public class MusicManager {

    private final List<String> trackOrder;
    private final Map<String, SoundInfo> soundMap;
    private final SoundSystem soundSystem;

    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    public MusicManager(Map<String, SoundInfo> sounds, SoundSystem soundSystem) {
        this.soundMap = sounds;
        this.soundSystem = soundSystem;
        this.trackOrder = new ArrayList<>(sounds.keySet());
    }

    /**
     * Start playing music
     */
    public void start() {
        if (!trackOrder.isEmpty() && !isPlaying) {
            playCurrentTrack();
        }
    }

    private void playCurrentTrack() {
        if (isPlaying || currentTrackIndex >= trackOrder.size()) return;

        String currentTrack = trackOrder.get(currentTrackIndex);
        SoundInfo info = soundMap.get(currentTrack);

        try {
            isPlaying = true;
            soundSystem.play(info, () -> {
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
        if (!soundMap.containsKey(trackName)) {
            RainLogger.RAIN_LOGGER.warn("Tried to set unknown track as next: {}", trackName);
            return;
        }

        // Prevent duplicates by removing if it already exists in the list
        trackOrder.remove(trackName);

        // Insert right after the current track index
        int insertIndex = Math.min(currentTrackIndex + 1, trackOrder.size());
        trackOrder.add(insertIndex, trackName);

        RainLogger.RAIN_LOGGER.info("Inserted track '{}' to play next (after index {}).", trackName, currentTrackIndex);
    }

    /**
     * Sets the starting track that should play first.
     *
     * @param trackName The name of the track to start with (must exist in soundMap)
     */
    public void setStartingSound(String trackName) {
        if (!soundMap.containsKey(trackName)) {
            RainLogger.RAIN_LOGGER.warn("Tried to set unknown track as starting sound: {}", trackName);
            return;
        }

        trackOrder.remove(trackName); // Avoid duplicate
        trackOrder.add(0, trackName); // Insert at the beginning
        currentTrackIndex = 0;        // Reset index to start with this track

        RainLogger.RAIN_LOGGER.info("Set starting track to '{}'", trackName);
    }

    /**
     * Get the currently playing track name.
     *
     * @return The name of the track that is currently playing or null if no track is playing.
     */
    public String getCurrentTrackName() {
        if (currentTrackIndex >= 0 && currentTrackIndex < trackOrder.size()) {
            return trackOrder.get(currentTrackIndex);
        }
        return null; // Return null if no track is currently playing.
    }

}
