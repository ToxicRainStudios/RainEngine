package com.toxicrain.sound.music;

import com.toxicrain.sound.SoundInfo;

import static org.lwjgl.openal.AL10.*;

public class MusicTrack {
    public SoundInfo soundInfo;
    public int sourceId;
    public float targetVolume = 1.0f;
    public boolean isPlaying = false;

    public MusicTrack(SoundInfo soundInfo, int sourceId) {
        this.soundInfo = soundInfo;
        this.sourceId = sourceId;
    }

    public void play(boolean loop) {
        alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
        alSourcei(sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        alSourcef(sourceId, AL_GAIN, 0.0f); // Start muted
        alSourcePlay(sourceId);
        isPlaying = true;
    }

    public void stop() {
        alSourceStop(sourceId);
        isPlaying = false;
    }

    public void setVolume(float volume) {
        alSourcef(sourceId, AL_GAIN, volume);
    }

    public float getVolume() {
        return alGetSourcef(sourceId, AL_GAIN);
    }
}

