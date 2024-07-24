package com.toxicrain.sound;

import com.toxicrain.util.FileUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundSystem {
    private long device;
    private long context;
    private boolean isPlaying;
    private int sourceId;

    public void init() {
        initOpenAL();
        isPlaying = false;
        sourceId = createSoundSource();
    }

    private void initOpenAL() {
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
        if (!alcMakeContextCurrent(context)) {
            throw new IllegalStateException("Failed to make OpenAL context current.");
        }

        // Create OpenAL capabilities
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        AL.createCapabilities(alcCapabilities);
    }

    public int loadSound(String filePath) {
        int bufferId = alGenBuffers();
        if (bufferId == 0) {
            throw new IllegalStateException("Failed to generate OpenAL buffer.");
        }

        try {
            ByteBuffer wavBuffer = FileUtils.ioResourceToByteBuffer(filePath, 1024);
            SoundInfo wavData = WAVDecoder.decode(wavBuffer);
            alBufferData(bufferId, wavData.format, wavData.data, wavData.samplerate);
            wavData.free();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sound file.", e);
        }

        return bufferId;
    }

    public int createSoundSource() {
        int sourceId = alGenSources();
        if (sourceId == 0) {
            int error = alGetError();
            throw new IllegalStateException("Failed to generate OpenAL source. Error code: " + error);
        }
        return sourceId;
    }

    public void play(int bufferId) {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state != AL_PLAYING && !isPlaying) {
            alSourcei(sourceId, AL_BUFFER, bufferId);
            alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_PLAYING) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public void cleanup(int bufferId) {
        stop();
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    public void cleanup() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }

}
