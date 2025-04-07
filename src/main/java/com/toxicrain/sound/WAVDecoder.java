package com.toxicrain.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.openal.AL10.*;


/**
 * Decodes a wav and converts it into a {@link WavInfo}
 *
 * @author strubium
 */
public class WAVDecoder {
    public static WavInfo decode(ByteBuffer buffer) throws IOException {
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (buffer.getInt() != 0x46464952) // "RIFF" in little-endian
            throw new IOException("Not a valid WAV file");

        buffer.getInt(); // Skip file size

        if (buffer.getInt() != 0x45564157) // "WAVE" in little-endian
            throw new IOException("Not a valid WAV file");

        boolean fmtChunkFound = false;
        boolean dataChunkFound = false;
        int format = 0;
        int sampleRate = 0;
        int numChannels = 0;
        int bitsPerSample = 0;
        int dataSize = 0;
        ByteBuffer data = null;

        while (buffer.remaining() > 0) {
            int chunkId = buffer.getInt();
            int chunkSize = buffer.getInt();

            switch (chunkId) {
                case 0x20746D66: // "fmt "
                    fmtChunkFound = true;
                    int audioFormat = buffer.getShort();
                    numChannels = buffer.getShort();
                    sampleRate = buffer.getInt();
                    buffer.getInt(); // Byte rate
                    buffer.getShort(); // Block align
                    bitsPerSample = buffer.getShort();
                    if (chunkSize > 16) {
                        buffer.position(buffer.position() + chunkSize - 16); // Skip extra fmt bytes
                    }
                    break;
                case 0x61746164: // "data"
                    dataChunkFound = true;
                    dataSize = chunkSize;
                    data = ByteBuffer.allocateDirect(dataSize);
                    for (int i = 0; i < dataSize; i++) {
                        data.put(buffer.get());
                    }
                    data.flip();
                    break;
                default:
                    buffer.position(buffer.position() + chunkSize); // Skip other chunks
                    break;
            }

            if (fmtChunkFound && dataChunkFound) {
                break;
            }
        }

        if (!fmtChunkFound || !dataChunkFound) {
            throw new IOException("Invalid WAV file: missing 'fmt ' or 'data' chunk");
        }

        switch (numChannels) {
            case 1:
                switch (bitsPerSample) {
                    case 8:
                        format = AL_FORMAT_MONO8;
                        break;
                    case 16:
                        format = AL_FORMAT_MONO16;
                        break;
                    default:
                        throw new IOException("Unsupported WAV format: " + numChannels + " channels, " + bitsPerSample + " bits per sample");
                }
                break;
            case 2:
                switch (bitsPerSample) {
                    case 8:
                        format = AL_FORMAT_STEREO8;
                        break;
                    case 16:
                        format = AL_FORMAT_STEREO16;
                        break;
                    default:
                        throw new IOException("Unsupported WAV format: " + numChannels + " channels, " + bitsPerSample + " bits per sample");
                }
                break;
            default:
                throw new IOException("Unsupported WAV format: " + numChannels + " channels");
        }

        double durationSeconds = (double) dataSize / (sampleRate * numChannels * (bitsPerSample / 8));
        return new WavInfo(data, format, sampleRate, durationSeconds);
    }
}
