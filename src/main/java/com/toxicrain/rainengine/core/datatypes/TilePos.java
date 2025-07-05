package com.toxicrain.rainengine.core.datatypes;

import com.toxicrain.rainengine.core.datatypes.vector.Vector3;

public class TilePos extends Vector3 {

    public TilePos(float x, float y, float z) {
        super(x, y, z);
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
