package com.toxicrain.rainengine.core.datatypes.vector;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Vector2 {
    public float x;
    public float y;

    public Vector2 update(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 scale(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize() {
        float mag = magnitude();
        if (mag == 0) return new Vector2(0, 0);
        return new Vector2(x / mag, y / mag);
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
}

