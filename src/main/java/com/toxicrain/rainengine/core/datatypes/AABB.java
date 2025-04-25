package com.toxicrain.rainengine.core.datatypes;

/**
 * The Axis Aligned Bounding Box
 */
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

    public void combine(AABB b) {
        float combinedMinX = Math.min(this.minX, b.minX);
        float combinedMinY = Math.min(this.minY, b.minY);
        float combinedMaxX = Math.max(this.maxX, b.maxX);
        float combinedMaxY = Math.max(this.maxY, b.maxY);

        this.minX = combinedMinX;
        this.minY = combinedMinY;
        this.maxX = combinedMaxX;
        this.maxY = combinedMaxY;
    }

    public void update(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean contains(float x, float y) {
        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY;
    }

    public boolean contains(TilePos pos) {
        return contains(pos.x, pos.y);
    }
}
