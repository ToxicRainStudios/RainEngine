package com.toxicrain.sound;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCapabilities;

import static org.lwjgl.openal.AL10.AL_BUFFER;

public class SoundSource {
    private int sourceId;

    public SoundSource() {
        sourceId = AL10.alGenSources();
        if (sourceId == 0) {
            throw new IllegalStateException("Failed to generate OpenAL source.");
        }
    }

    public void play(int bufferId) {
        AL10.alSourcei(sourceId, AL_BUFFER, bufferId);
        AL10.alSourcePlay(sourceId);
    }

    public void cleanup() {
        AL10.alDeleteSources(sourceId);
    }
}