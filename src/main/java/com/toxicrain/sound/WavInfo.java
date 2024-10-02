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
    public final int samplerate;

    public WavInfo(ByteBuffer data, int format, int samplerate) {
        this.data = data;
        this.format = format;
        this.samplerate = samplerate;
    }

    public void free() {
        data.clear();
    }
}