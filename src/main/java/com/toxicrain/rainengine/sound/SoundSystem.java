package com.toxicrain.rainengine.sound;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.eventbus.events.load.sound.SoundInfoLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.sound.SoundSystemLoadEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.resources.ResourceManager;
import com.toxicrain.rainengine.util.FileUtils;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundSystem {
    private long device;
    private long context;

    @Getter
    private Queue<Integer> availableSources;
    private static final int MAX_SOURCES = 32;
    private float currentVolume = 1.0f;
    private boolean isFading = false;

    public SoundSystem() {
        SmeagleBus.getInstance().post(new SoundSystemLoadEvent(this));
    }

    /**
     * Loads all sounds from the /sound directory into ResourceManager
     */
    public static void initSounds() {
        String soundDirectory = FileUtils.getCurrentWorkingDirectory("resources/sound");

        try {
            Files.walk(Paths.get(soundDirectory))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".wav"))
                    .forEach(path -> {
                        String filePath = path.toString();
                        Resource location = computeResourceLocation(soundDirectory, path);

                        try {
                            ResourceManager.load(SoundInfo.class, location, filePath);
                        } catch (Exception e) {
                            RainLogger.RAIN_LOGGER.error("Failed to load sound: {}", path.getFileName(), e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sounds from directory: " + soundDirectory, e);
        }

        RainLogger.RAIN_LOGGER.info("Sound loading complete.");
    }

    /**
     * Retrieve a sound by Resource
     */
    public static SoundInfo getSound(Resource location) {
        SoundInfo sound = ResourceManager.get(SoundInfo.class, location);
        if (sound == null) {
            throw new IllegalStateException("Cannot find sound: " + location);
        }
        return sound;
    }

    /**
     * Retrieve a sound by string path
     */
    public static SoundInfo getSound(String location) {
        return getSound(new Resource(location));
    }

    public void init() {
        initOpenAL();
        initializeSourcePool();
    }

    private void initOpenAL() {
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }

        context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }

        if (!alcMakeContextCurrent(context)) {
            throw new IllegalStateException("Failed to make OpenAL context current.");
        }

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        AL.createCapabilities(alcCapabilities);
    }

    private void initializeSourcePool() {
        availableSources = new LinkedList<>();
        for (int i = 0; i < MAX_SOURCES; i++) {
            int sourceId = alGenSources();
            if (sourceId == 0) {
                throw new IllegalStateException("Failed to generate OpenAL source.");
            }
            availableSources.add(sourceId);
        }
    }

    private int getAvailableSource() {
        Integer sourceId = availableSources.poll();
        if (sourceId == null) {
            throw new IllegalStateException("No available sources to play sound.");
        }
        return sourceId;
    }

    public void releaseSource(int sourceId) {
        availableSources.offer(sourceId);
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
            alBufferData(bufferId, wavData.format, wavData.data, wavData.sampleRate);

            long fileSize = FileUtils.getFileSize(filePath);
            RainLogger.RAIN_LOGGER.debug("Loaded sound: {} (File Size: {} bytes, Format: {})", filePath, fileSize, wavData.format);
        } catch (FileNotFoundException e) {
            RainLogger.RAIN_LOGGER.error("File not found: {}", filePath);
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Error reading file: {}", filePath);
        } catch (Exception e) {
            RainLogger.RAIN_LOGGER.error("Error processing sound file: {}", e.getMessage());
        }

        SoundInfo soundInfo = new SoundInfo(wavData, bufferId);
        SmeagleBus.getInstance().post(new SoundInfoLoadEvent(soundInfo));
        return soundInfo;
    }

    public void play(SoundInfo soundInfo) {
        int sourceId = getAvailableSource();
        alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
        alSourcePlay(sourceId);
    }

    public void play(SoundInfo soundInfo, boolean fadeIn, float fadeDuration) {
        if (fadeIn) {
            fadeIn(soundInfo, fadeDuration);
        } else {
            play(soundInfo);
        }
    }

    public void play(SoundInfo soundInfo, Runnable onEnd) {
        int sourceId = getAvailableSource();
        alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
        alSourcePlay(sourceId);

        new Thread(() -> {
            int state;
            do {
                state = alGetSourcei(sourceId, AL_SOURCE_STATE);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            } while (state == AL_PLAYING);

            releaseSource(sourceId);
            if (onEnd != null) {
                onEnd.run();
            }
        }, "SoundPlaybackMonitor").start();
    }

    public void stop() {
        for (Integer sourceId : availableSources) {
            int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
            if (state == AL_PLAYING) {
                alSourceStop(sourceId);
                releaseSource(sourceId);
            }
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
        alDeleteSources(soundInfo.bufferId);
        alDeleteBuffers(soundInfo.bufferId);
    }

    public void cleanup() {
        for (Integer sourceId : availableSources) {
            alDeleteSources(sourceId);
        }
        availableSources.clear();

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
                int sourceId = getAvailableSource();
                alSourcei(sourceId, AL_BUFFER, soundInfo.bufferId);
                setVolume(0.0f);
                alSourcePlay(sourceId);
                isFading = true;

                float increment = 1.0f / (duration * 1000 / 10);

                while (currentVolume < 1.0f && isFading) {
                    currentVolume = Math.min(1.0f, currentVolume + increment);
                    setVolume(currentVolume);
                    Thread.sleep(10);
                }

                releaseSource(sourceId);
                isFading = false;
            } catch (InterruptedException e) {
                RainLogger.RAIN_LOGGER.error("Fade-in interrupted.");
            }
        }).start();
    }

    private void fadeOut(float duration) {
        new Thread(() -> {
            try {
                isFading = true;
                float decrement = currentVolume / (duration * 1000 / 10);

                while (currentVolume > 0.0f && isFading) {
                    currentVolume = Math.max(0.0f, currentVolume - decrement);
                    setVolume(currentVolume);
                    Thread.sleep(10);
                }

                stop();
                isFading = false;
            } catch (InterruptedException e) {
                RainLogger.RAIN_LOGGER.error("Fade-out interrupted.");
            }
        }).start();
    }

    public void setVolume(float volume) {
        currentVolume = volume;
        for (Integer sourceId : availableSources) {
            alSourcef(sourceId, AL_GAIN, volume);
        }
    }

    public float getVolume() {
        return currentVolume;
    }

    private static Resource computeResourceLocation(String baseDirectory, Path path) {
        Path relativePath = Paths.get(baseDirectory).relativize(path);
        String[] pathParts = relativePath.toString().replace("\\", "/").split("/");

        String namespace;
        String resourcePath;

        if (pathParts.length >= 2) {
            namespace = pathParts[0];
            resourcePath = String.join("/", pathParts).substring(namespace.length() + 1).replaceFirst("[.][^.]+$", "");
        } else {
            namespace = "rainengine";
            resourcePath = relativePath.toString().replace("\\", "/").replaceFirst("[.][^.]+$", "");
        }

        return new Resource(namespace, resourcePath);
    }
}
