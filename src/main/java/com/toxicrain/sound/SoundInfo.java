package com.toxicrain.sound;

/**
 * The SoundInfo class provides information about the given sound
 * like the WavInfo and the id for the buffer
 *
 * @author strubium
 */
public class SoundInfo {
    public final WavInfo wavInfo;
    public final int bufferId;

    public SoundInfo(WavInfo wavInfo, int bufferId) {
        this.wavInfo = wavInfo;
        this.bufferId = bufferId;
    }

    // Optionally, you can add other methods to retrieve information about the WAV, like format or sample rate
}
