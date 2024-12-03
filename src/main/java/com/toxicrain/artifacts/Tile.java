package com.toxicrain.artifacts;

import com.toxicrain.core.AABB;

import java.util.ArrayList;

public class Tile {
    /**
     * List of AABB objects representing tile collision bounds.
     */
    public static ArrayList<AABB> aabbs = new ArrayList<>();
    public static ArrayList<Character> mapDataType = new ArrayList<>();

    /**
     * Adds collision information for a tile at specified coordinates.
     *
     * @param yCoordinate The Y coordinate of the tile.
     * @param xCoordinate The X coordinate of the tile.
     */
    public static void addCollision(int yCoordinate, int xCoordinate) {
        float extentTop = ((float) yCoordinate * -2) + 1.1f;
        float extentBottom = ((float) yCoordinate * -2) - 1.1f;
        float extentLeft = ((float) xCoordinate * 2) - 1.1f;
        float extentRight = ((float) xCoordinate * 2) + 1.1f;

        AABB newAABB = new AABB(extentLeft, extentBottom, extentRight, extentTop);
        aabbs.add(newAABB);
    }
}
