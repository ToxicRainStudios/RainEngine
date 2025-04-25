package com.toxicrain.rainengine.core.datatypes;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public float distanceTo(TilePos other) {
        float dx = other.x - this.x;
        float dy = other.y - this.y;
        float dz = other.z - this.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TilePos )) return false;
        else{
            TilePos other = (TilePos) obj;
            return (int) x == (int) other.x && (int) y == (int) other.y && (int) z == (int) other.z;
        }
    }
}
