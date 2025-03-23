package com.toxicrain.sound.music;

import com.toxicrain.core.RainLogger;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;

public class MusicManager {
    private MusicTrack calmTrack;
    private MusicTrack combatTrack;
    private MusicTrack tensionLayer;

    private String currentState = "CALM";

    public MusicManager(SoundInfo calm, SoundInfo combat, SoundInfo tension, SoundSystem soundSystem) {
        calmTrack = new MusicTrack(calm, soundSystem.createSoundSource());
        combatTrack = new MusicTrack(combat, soundSystem.createSoundSource());
        tensionLayer = new MusicTrack(tension, soundSystem.createSoundSource());

        calmTrack.play(true);
        combatTrack.play(true);
        tensionLayer.play(true);
    }

    public void update(String gameState) {
        if (!gameState.equals(currentState)) {
            switch (gameState) {
                case "CALM":
                    fadeTo(calmTrack, 1.0f);
                    fadeTo(combatTrack, 0.0f);
                    fadeTo(tensionLayer, 0.0f);
                    break;
                case "COMBAT":
                    fadeTo(calmTrack, 0.0f);
                    fadeTo(combatTrack, 1.0f);
                    fadeTo(tensionLayer, 0.5f);
                    break;
                case "TENSE":
                    fadeTo(calmTrack, 0.0f);
                    fadeTo(combatTrack, 0.0f);
                    fadeTo(tensionLayer, 1.0f);
                    break;
            }
            currentState = gameState;
        }
    }

    private void fadeTo(MusicTrack track, float targetVolume) {
        new Thread(() -> {
            float current = track.getVolume();
            float step = 0.01f * (targetVolume > current ? 1 : -1);
            try {
                while (Math.abs(targetVolume - current) > 0.01f) {
                    current += step;
                    current = Math.max(0.0f, Math.min(1.0f, current));
                    track.setVolume(current);
                    Thread.sleep(10);
                }
                track.setVolume(targetVolume);
            } catch (InterruptedException e) {
                RainLogger.printERROR("Fade thread interrupted");
            }
        }).start();
    }
}

