package com.toxicrain.artifacts;

public class AABB {
    public float minX, minY; // Minimum corner
    public float maxX, maxY; // Maximum corner

    public AABB(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean intersects(AABB other) {
        return this.maxX > other.minX &&
                this.minX < other.maxX &&
                this.maxY > other.minY &&
                this.minY < other.maxY;
    }
}

