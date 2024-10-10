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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundSystem {
    private long device;
    private long context;
    private int sourceId;
    private float currentVolume = 1.0f;  // Default volume (full)
    private boolean isFading = false;

    // Map to store all loaded sounds with file names
    private static final Map<String, SoundInfo> sounds = new HashMap<>();

    /**
     * Init the sounds by dynamically loading all images from the /sound folder
     */
    public static void initSounds() {
        String soundDirectory = FileUtils.getCurrentWorkingDirectory("resources/sound"); // Directory containing sounds

        try {
            // Get all files in the images directory
            Files.walk(Paths.get(soundDirectory))
                    .filter(Files::isRegularFile) // Only regular files, not directories
                    .filter(path -> {
                        // Filter out files that are images (png, jpg, jpeg)
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".wav");
                    })
                    .forEach(path -> {
                        // Load each sound
                        String filePath = path.toString();
                        SoundInfo sound = loadSound(filePath);
                        // Store the sound with its file name (without extension) as the key
                        String soundName = path.getFileName().toString().replaceFirst("[.][^.]+$", ""); // remove extension
                        sounds.put(soundName, sound);
                        Logger.printLOG("Loaded sound: " + soundName);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sounds from directory: " + soundDirectory, e);
        }
    }

    /**
     * Retrieve a sound by its name (without extension)
     *
     * @param soundName Name of the sound file (without extension)
     * @return SoundInfo object for the corresponding sound, or null if not found
     */
    public static SoundInfo getSound(String soundName) {
        if (!sounds.containsKey(soundName)) {
            Logger.printLOG("Sound not found: " + soundName);
            return null;  // Return null or throw an exception if sound is not found
        }
        return sounds.get(soundName);
    }

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

    public static SoundInfo loadSound(String filePath) {
        int bufferId = alGenBuffers();
        if (bufferId == 0) {
            throw new IllegalStateException("Failed to generate an OpenAL buffer.");
        }

        WavInfo wavData = null;
        try {
            ByteBuffer wavBuffer = FileUtils.ioResourceToByteBuffer(FileUtils.getCurrentWorkingDirectory(filePath));
            wavData = WAVDecoder.decode(wavBuffer);
            alBufferData(bufferId, wavData.format, wavData.data, wavData.samplerate);

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

        // Return the SoundInfo containing the WavInfo and bufferId
        return new SoundInfo(wavData, bufferId);
    }


    public int createSoundSource() {
        int sourceId = alGenSources();
        if (sourceId == 0) {
            int error = alGetError();
            throw new IllegalStateException("Failed to generate OpenAL source. Error code: " + error);
        }
        return sourceId;
    }

    public void play(SoundInfo soundInfo) {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state != AL_PLAYING) {
            Logger.printLOG("Playing sound");
            alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
            alSourcePlay(sourceId);
        }
    }

    public void play(SoundInfo soundInfo, boolean fadeIn, float fadeDuration) {
        if (fadeIn) {
            fadeIn(soundInfo, fadeDuration);
        } else {
            play(soundInfo);
        }
    }

    public void stop() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_PLAYING) {
            alSourceStop(sourceId);
        }
    }

    public void stop(boolean fadeOut, float fadeDuration) {
        if (fadeOut) {
            fadeOut(fadeDuration);
        } else {
            stop();
        }
    }

    public void cleanup(SoundInfo soundInfo) {
        stop();
        alDeleteSources(sourceId);
        alDeleteBuffers(soundInfo.bufferId);
    }

    public void cleanup() {
        if (context != NULL) {
            alcDestroyContext(context);
            context = NULL;
        }
        if (device != NULL) {
            alcCloseDevice(device);
            device = NULL;
        }
    }

    private void fadeIn(SoundInfo soundInfo, float duration) {
        new Thread(() -> {
            try {
                alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
                setVolume(0.0f);  // Start at zero volume
                alSourcePlay(sourceId);
                isFading = true;

                float increment = 1.0f / (duration * 1000 / 10);  // Every 10ms, increase volume

                while (currentVolume < 1.0f && isFading) {
                    currentVolume = Math.min(1.0f, currentVolume + increment);
                    setVolume(currentVolume);
                    Thread.sleep(10);  // Update volume every 10ms
                }

                isFading = false;
            } catch (InterruptedException e) {
                Logger.printERROR("Fade-in interrupted.");
            }
        }).start();
    }

    private void fadeOut(float duration) {
        new Thread(() -> {
            try {
                isFading = true;
                float decrement = currentVolume / (duration * 1000 / 10);  // Every 10ms, decrease volume

                while (currentVolume > 0.0f && isFading) {
                    currentVolume = Math.max(0.0f, currentVolume - decrement);
                    setVolume(currentVolume);
                    Thread.sleep(10);  // Update volume every 10ms
                }

                alSourceStop(sourceId);
                isFading = false;
            } catch (InterruptedException e) {
                Logger.printERROR("Fade-out interrupted.");
            }
        }).start();
    }

    public void setVolume(float volume) {
        currentVolume = volume;
        alSourcef(sourceId, AL_GAIN, volume);
    }

    public float getVolume() {
        return currentVolume;
    }
}
