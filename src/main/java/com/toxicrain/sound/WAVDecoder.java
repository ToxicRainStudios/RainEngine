package com.toxicrain.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class WAVDecoder {
    public static SoundInfo decode(ByteBuffer buffer) throws IOException {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (buffer.getInt() != 0x46464952) // "RIFF" in little-endian
            throw new IOException("Not a valid WAV file");

        buffer.getInt(); // Skip file size

        if (buffer.getInt() != 0x45564157) // "WAVE" in little-endian
            throw new IOException("Not a valid WAV file");

        if (buffer.getInt() != 0x20746D66) // "fmt " in little-endian
            throw new IOException("Not a valid WAV file");

        int fmtChunkSize = buffer.getInt();
        int audioFormat = buffer.getShort();
        int numChannels = buffer.getShort();
        int sampleRate = buffer.getInt();
        buffer.getInt(); // Byte rate
        buffer.getShort(); // Block align
        int bitsPerSample = buffer.getShort();

        if (fmtChunkSize > 16)
            buffer.position(buffer.position() + fmtChunkSize - 16); // Skip extra bytes

        int format;
        if (numChannels == 1) {
            if (bitsPerSample == 8) {
                format = AL_FORMAT_MONO8;
            } else if (bitsPerSample == 16) {
                format = AL_FORMAT_MONO16;
            } else {
                throw new IOException("Unsupported WAV format");
            }
        } else if (numChannels == 2) {
            if (bitsPerSample == 8) {
                format = AL_FORMAT_STEREO8;
            } else if (bitsPerSample == 16) {
                format = AL_FORMAT_STEREO16;
            } else {
                throw new IOException("Unsupported WAV format");
            }
        } else {
            throw new IOException("Unsupported WAV format");
        }

        if (buffer.getInt() != 0x61746164) // "data" in little-endian
            throw new IOException("Not a valid WAV file");

        int dataSize = buffer.getInt();
        ByteBuffer data = ByteBuffer.allocateDirect(dataSize);
        for (int i = 0; i < dataSize; i++) {
            data.put(buffer.get());
        }
        data.flip();

        return new SoundInfo(data, format, sampleRate);
    }
}
