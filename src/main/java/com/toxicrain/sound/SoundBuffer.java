package com.toxicrain.sound;

import org.lwjgl.openal.AL10;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SoundBuffer {
    private int bufferId;

    public SoundBuffer(String filePath) {
        bufferId = AL10.alGenBuffers();
        if (bufferId == 0) {
            throw new IllegalStateException("Failed to generate OpenAL buffer.");
        }

        try {
            ByteBuffer wavBuffer = ioResourceToByteBuffer(filePath, 1024);
            SoundInfo wavData = WAVDecoder.decode(wavBuffer);
            AL10.alBufferData(bufferId, wavData.format, wavData.data, wavData.samplerate);
            wavData.free();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sound file.", e);
        }
    }

    public int getBufferId() {
        return bufferId;
    }

    public void cleanup() {
        AL10.alDeleteBuffers(bufferId);
    }

    // Utility method to load file into ByteBuffer
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        Path path = Paths.get(resource);
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("File not readable: " + resource);
        }
        ByteBuffer buffer;
        try (SeekableByteChannel fc = Files.newByteChannel(path)) {
            buffer = ByteBuffer.allocateDirect((int) fc.size() + 1);
            while (fc.read(buffer) != -1);
        }
        buffer.flip();
        return buffer;
    }
}
