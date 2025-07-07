package com.toxicrain.rainengine.core.registries.tiles;

import com.toxicrain.rainengine.core.datatypes.AABB;

import java.util.ArrayList;

public class Tile {
    /**
     * List of AABB objects representing tile collision bounds.
     */
    public static ArrayList<AABB> aabbs = new ArrayList<>();
    public static ArrayList<Character> mapDataType = new ArrayList<>();

    /**
     * Combines touching AABBs in the list without breaking collision behavior.
     */
    public static void combineTouchingAABBs() {
        ArrayList<AABB> combinedAABBs = new ArrayList<>();
        boolean[] combined = new boolean[aabbs.size()]; // To track already combined AABBs

        for (int i = 0; i < aabbs.size(); i++) {
            if (combined[i]) continue; // Skip if already combined
            AABB current = new AABB(aabbs.get(i).minX, aabbs.get(i).minY, aabbs.get(i).maxX, aabbs.get(i).maxY);

            for (int j = i + 1; j < aabbs.size(); j++) {
                if (combined[j]) continue;
                AABB other = aabbs.get(j);

                // Check if AABBs are touching
                if (areTouching(current, other)) {
                    current = mergeAABBs(current, other); // Create a new combined AABB
                    combined[j] = true; // Mark the other AABB as combined
                }
            }

            combined[i] = true; // Mark the current AABB as combined
            combinedAABBs.add(current); // Add the final merged AABB
        }

        aabbs = combinedAABBs; // Update the original list
    }

    /**
     * Checks if two AABBs are touching or overlapping.
     *
     * @param a The first AABB.
     * @param b The second AABB.
     * @return True if the AABBs are touching or overlapping, false otherwise.
     */
    private static boolean areTouching(AABB a, AABB b) {
        // Check for overlap or touching on all sides
        return (a.minX <= b.maxX && a.maxX >= b.minX && a.minY <= b.maxY && a.maxY >= b.minY) &&
                (a.maxX == b.minX || a.minX == b.maxX || a.maxY == b.minY || a.minY == b.maxY);
    }

    /**
     * Merges two AABBs into a new AABB.
     *
     * @param a The first AABB.
     * @param b The second AABB.
     * @return A new AABB that encompasses both input AABBs.
     */
    private static AABB mergeAABBs(AABB a, AABB b) {
        return new AABB(
                Math.min(a.minX, b.minX),
                Math.min(a.minY, b.minY),
                Math.max(a.maxX, b.maxX),
                Math.max(a.maxY, b.maxY)
        );
    }

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

    public static void clearCollision() {
        aabbs.clear();
    }
}
