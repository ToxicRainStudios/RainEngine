package com.toxicrain.sound.music;

import com.toxicrain.core.RainLogger;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;

public class MusicManager {
    private MusicTrack A1;
    private MusicTrack A2;
    private MusicTrack A3;
    private MusicTrack B1;
    private MusicTrack Breakdown;
    private MusicTrack Intro;
    private MusicTrack Panic1;
    private MusicTrack Panic2;
    private MusicTrack Panic3;

    private String currentState = "CALM";

    public MusicManager(SoundInfo A1s, SoundInfo A2s, SoundInfo A3s, SoundInfo B1s,SoundInfo Breakdowns,SoundInfo Intros,SoundInfo Panic1s,SoundInfo Panic2s,SoundInfo Panic3s, SoundSystem soundSystem) {
        A1 = new MusicTrack(A1s, soundSystem.createSoundSource());
        A2 = new MusicTrack(A2s, soundSystem.createSoundSource());
        A3 = new MusicTrack(A3s, soundSystem.createSoundSource());
        B1 = new MusicTrack(B1s, soundSystem.createSoundSource());
        Breakdown = new MusicTrack(Breakdowns, soundSystem.createSoundSource());
        Intro = new MusicTrack(Intros, soundSystem.createSoundSource());
        Panic1 = new MusicTrack(Panic1s, soundSystem.createSoundSource());
        Panic2 = new MusicTrack(Panic2s, soundSystem.createSoundSource());
        Panic3 = new MusicTrack(Panic3s, soundSystem.createSoundSource());


    }

    public void update(String gameState) {
        int songState = 0;
        if (!gameState.equals(currentState)) {
            switch (gameState) {
                case "CALM0":
                    Intro.play(false);
                    break;
                case "CALM1":
                   A1.play(false);
                    break;
                case "CALM2":
                    A2.play(false);
                    break;
                case "CALM3":
                    A3.play(false);
                    break;
                case "BREAKDOWN":
                    Breakdown.play(false);
                    break;
                case "COMBAT":
                    B1.play(false);
                    break;
                case "PANIC1":
                    Panic1.play(false);
                    fadeTo(B1, 0.0f);
                    fadeTo(Panic1, 1.0f);
                    break;
                case "PANIC2":
                    Panic2.play(false);
                    fadeTo(B1, 0.0f);
                    fadeTo(Panic2, 1.0f);
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

