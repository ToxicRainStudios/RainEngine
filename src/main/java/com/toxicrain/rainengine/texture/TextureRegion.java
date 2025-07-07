package com.toxicrain.rainengine.texture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TextureRegion {
    private final TextureInfo textureInfo; // The full texture info
    private final float u0, v0, u1, v1;
}
