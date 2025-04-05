package com.toxicrain.sound.music;

import com.toxicrain.core.RainLogger;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;

public class MusicManager {

    private SoundInfo A1;
    private SoundInfo A2;
    private SoundInfo A3;
    private SoundInfo B1;
    private SoundInfo Breakdown;
    private SoundInfo Intro;
    private SoundInfo Panic1;
    private SoundInfo Panic2;
    private SoundInfo Panic3;
    private SoundSystem soundSystem;
    private String currentState = "CALM";

    public MusicManager(SoundInfo A1s, SoundInfo A2s, SoundInfo A3s, SoundInfo B1s, SoundInfo Breakdowns, SoundInfo Intros,SoundInfo Panic1s,SoundInfo Panic2s,SoundInfo Panic3s, SoundSystem soundSystem) {
        this.A1 = A1s;
        this.A2 = A2s;
        this.A3 = A3s;
        this.B1 = B1s;
        this.Breakdown = Breakdowns;
        this.Intro = Intros;
        this.Panic1 = Panic1s;
        this.Panic2 = Panic2s;
        this.Panic3 = Panic3s;

        this.soundSystem = soundSystem;


    }

    public void update(String gameState) {
        if (!gameState.equals(currentState)) {
            switch (gameState) {
                case "CALM0":
                    soundSystem.play(Intro);
                    break;
                case "CALM1":
                    soundSystem.play(A1);
                    break;
                case "CALM2":
                    soundSystem.play(A2);
                    break;
                case "CALM3":
                    soundSystem.play(A3);
                    break;
                case "BREAKDOWN":
                    soundSystem.play(Breakdown);
                    break;
                case "COMBAT":
                    soundSystem.play(B1);
                    break;
                case "PANIC1":
                    soundSystem.play(Panic1);
                    break;
                case "PANIC2":
                    soundSystem.play(Panic2);
                    break;

            }
            currentState = gameState;
        }

    }
}

