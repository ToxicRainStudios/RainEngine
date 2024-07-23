/*package com.toxicrain.util.sound;


import org.lwjgl.openal.

import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;


public class SoundBuffer {
    private final int bufferId;

    public SoundBuffer() throws Exception{

        this.bufferId = alGenBuffers();
        try(STBVorbisInfo info = STBVorbisInfo.malloc()){
            ShortBuffer pcm = readVorbis(file, 32 * 1024, info);


            alBufferData(buffer, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }


        }
        public int getBufferId(){

        return this.bufferId;
    }
    public void cleanup(){

        alDeleteBuffers(this.bufferId);
    }





}

 */
