package com.toxicrain.rainengine.core.datatypes;

import lombok.Getter;

public enum Size {
        MICROSCOPIC(1f),
        TINY(2f),
        SMALL(3.5f),
        /**This is the size used by the player, and should be the default*/
        AVERAGE(5f),
        BIG(7.5f);

    @Getter
    private final float size;

    Size(float size) {
        this.size = size;
    }

}
