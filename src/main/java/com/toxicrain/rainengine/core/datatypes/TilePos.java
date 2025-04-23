package com.toxicrain.rainengine.core.datatypes;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TilePos {

    public float x,y,z;

    public void update(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }
}
