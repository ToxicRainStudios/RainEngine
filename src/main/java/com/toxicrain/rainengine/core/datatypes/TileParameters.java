package com.toxicrain.rainengine.core.datatypes;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TileParameters {

    public Float angle = null;          // Optional rotation angle
    public Float posX = null;           // Optional reference point for rotation
    public Float posY = null;           // Optional reference point for rotation
    public float scaleX = 1.0f;         // Default scaling along the X-axis
    public float scaleY = 1.0f;         // Default scaling along the Y-axis
    public float[] color = null;        // Optional RGBA color array
    public List<float[]> lightPositions = null; // Optional light positions for dynamic lighting

}

