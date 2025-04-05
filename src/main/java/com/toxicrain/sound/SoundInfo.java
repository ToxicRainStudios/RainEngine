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

    /**
     * Make a new SoundInfo
     *
     * @param wavInfo The {@link WavInfo} class that holds basic info about the audio file
     * @param bufferId The Buffer ID used to play the sound
     */
    public SoundInfo(WavInfo wavInfo, int bufferId) {
        this.wavInfo = wavInfo;
        this.bufferId = bufferId;
    }

    public void free(){
        wavInfo.free();
    }
}
