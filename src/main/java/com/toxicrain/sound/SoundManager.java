package com.toxicrain.sound;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager {
    private long device;
    private long context;

    public void init() {
        // Open the default device
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        // Create the OpenAL context
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        // Make the context current
        alcMakeContextCurrent(context);

        // Create OpenAL capabilities
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);

        AL.createCapabilities(alcCapabilities);
    }

    public void cleanup() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
