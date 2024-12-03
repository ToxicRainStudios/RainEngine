package com.toxicrain.artifacts;

public enum Size {
        MICROSCOPIC(1f),
        TINY(2f),
        SMALL(3.5f),
        AVERAGE(5f),
        BIG(7.5f);

    private final float size;
    Size(float size) {
        this.size = size;
    }

}
