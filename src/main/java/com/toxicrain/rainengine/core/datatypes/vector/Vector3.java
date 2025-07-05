package com.toxicrain.rainengine.core.datatypes.vector;

import com.toxicrain.rainengine.core.datatypes.TilePos;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Vector3 {

    public float x, y, z;

    public Vector3(Vector3 other) {
        this(other.x, other.y, other.z);
    }

    public void update(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void subtract(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }

    public void multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        float mag = magnitude();
        if (mag != 0) {
            this.x /= mag;
            this.y /= mag;
            this.z /= mag;
        }
    }

    public Vector3 copy() {
        return new Vector3(this);
    }

    public float distanceTo(TilePos other) {
        float dx = other.x - this.x;
        float dy = other.y - this.y;
        float dz = other.z - this.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector3)) return false;
        Vector3 other = (Vector3) obj;
        return Float.compare(x, other.x) == 0 &&
                Float.compare(y, other.y) == 0 &&
                Float.compare(z, other.z) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        result = 31 * result + Float.hashCode(z);
        return result;
    }
}
