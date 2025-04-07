package com.toxicrain.sound;

import java.nio.ByteBuffer;


/**
 * The WavInfo class provides information about the given sound
 * such as the format and samplerate
 *
 * @author strubium
 */
public class WavInfo {
    public final ByteBuffer data;
    public final int format;
    public final int sampleRate;
    public final double durationSeconds;

    public WavInfo(ByteBuffer data, int format, int sampleRate, double durationSeconds) {
        this.data = data;
        this.format = format;
        this.sampleRate = sampleRate;
        this.durationSeconds = durationSeconds;
    }

    public void free() {
        data.clear();
    }
}