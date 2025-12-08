package com.toxicrain.rainengine.sound.music;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.sound.SoundInfo;
import com.toxicrain.rainengine.sound.SoundSystem;

import java.util.*;

public class MusicManager extends BaseInstanceable<MusicManager> {

    private List<String> trackOrder = new ArrayList<>();
    private Map<String, SoundInfo> soundMap = new HashMap<>();
    private final SoundSystem soundSystem;

    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    public static MusicManager getInstance() {
        return BaseInstanceable.getInstance(MusicManager.class);
    }

    private MusicManager() {
        this.soundSystem = SoundSystem.getInstance();
    }

    public void setSounds(Map<String, SoundInfo> newSounds) {
        this.soundMap = new HashMap<>(newSounds);
        this.trackOrder = new ArrayList<>(newSounds.keySet());
        this.currentTrackIndex = 0;

        RainLogger.RAIN_LOGGER.info("Sound map replaced at runtime.");
    }

    public void addOrUpdateSound(String name, SoundInfo info) {
        soundMap.put(name, info);
        if (!trackOrder.contains(name)) {
            trackOrder.add(name);
        }
        RainLogger.RAIN_LOGGER.info("Added/updated sound '{}'", name);
    }

    public void removeSound(String name) {
        soundMap.remove(name);
        int removedIndex = trackOrder.indexOf(name);

        if (removedIndex != -1) {
            trackOrder.remove(removedIndex);

            if (removedIndex <= currentTrackIndex && currentTrackIndex > 0) {
                currentTrackIndex--;
            }
        }

        RainLogger.RAIN_LOGGER.info("Removed sound '{}'", name);
    }

    /**
     * Start playing music
     */
    public void start() {
        if (!trackOrder.isEmpty() && !isPlaying) {
            playCurrentTrack();
        } else if (trackOrder.isEmpty()) {
            RainLogger.RAIN_LOGGER.warn("Cannot start music: track list is empty.");
        }
    }

    private void playCurrentTrack() {
        if (isPlaying || currentTrackIndex >= trackOrder.size()) return;

        String currentTrack = trackOrder.get(currentTrackIndex);
        SoundInfo info = soundMap.get(currentTrack);

        if (info == null) {
            RainLogger.RAIN_LOGGER.warn("Track '{}' has no SoundInfo. Skipping.", currentTrack);
            currentTrackIndex++;
            playCurrentTrack();
            return;
        }

        try {
            isPlaying = true;
            soundSystem.play(info, () -> {
                isPlaying = false;
                currentTrackIndex++;
                playCurrentTrack();
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
            RainLogger.RAIN_LOGGER.warn("Unknown track '{}'", trackName);
            return;
        }

        trackOrder.remove(trackName);
        trackOrder.add(Math.min(currentTrackIndex + 1, trackOrder.size()), trackName);

        RainLogger.RAIN_LOGGER.info("'{}' set to play next.", trackName);
    }

    /**
     * Sets the starting track that should play first.
     *
     * @param trackName The name of the track to start with (must exist in soundMap)
     */
    public void setStartingSound(String trackName) {
        if (!soundMap.containsKey(trackName)) {
            RainLogger.RAIN_LOGGER.warn("Unknown starting track '{}'", trackName);
            return;
        }

        trackOrder.remove(trackName);
        trackOrder.add(0, trackName);
        currentTrackIndex = 0;

        RainLogger.RAIN_LOGGER.info("Starting track set to '{}'", trackName);
    }

    public void setTrackOrder(List<String> newOrder) {
        for (String name : newOrder) {
            if (!soundMap.containsKey(name)) {
                RainLogger.RAIN_LOGGER.warn("Track order contains unknown sound '{}'. Ignoring.", name);
                return;
            }
        }

        this.trackOrder = new ArrayList<>(newOrder);
        this.currentTrackIndex = 0;

        RainLogger.RAIN_LOGGER.info("Track order replaced at runtime.");
    }

    public List<String> getTrackOrder() {
        return Collections.unmodifiableList(trackOrder);
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
        return null;
    }
}
