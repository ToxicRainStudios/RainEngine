package com.toxicrain.sound;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundSystem {
    private long device;
    private long context;
    private int sourceId;

    public void init() {
        initOpenAL();
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
            throw new IllegalStateException("Failed to generate a OpenAL buffer.");
        }

        try {
            ByteBuffer wavBuffer = FileUtils.ioResourceToByteBuffer(FileUtils.getCurrentWorkingDirectory(filePath));
            SoundInfo wavData = WAVDecoder.decode(wavBuffer);
            alBufferData(bufferId, wavData.format, wavData.data, wavData.samplerate);
            wavData.free();

            long fileSize = FileUtils.getFileSize(filePath);
            Logger.printLOG(String.format("Loaded sound: %s (File Size: %d bytes, Format: %d)", filePath, fileSize, wavData.format));
        } catch (FileNotFoundException e) {
            Logger.printERROR("File not found: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            Logger.printERROR("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            Logger.printERROR("Error processing sound file: " + e.getMessage());
            e.printStackTrace();
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
        if (state != AL_PLAYING) {
            Logger.printLOG("Playing sound");
            alSourcei(sourceId, AL_BUFFER, bufferId);
            alSourcePlay(sourceId);
        }
    }

    public void stop() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_PLAYING) {
            alSourceStop(sourceId);
        }
    }

    public void cleanup(int bufferId) {
        stop();
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    public void cleanup() {
        // Clean up OpenAL resources
        if (context != NULL) {
            alcDestroyContext(context);
            context = NULL;
        }
        if (device != NULL) {
            alcCloseDevice(device);
            device = NULL;
        }
    }
}