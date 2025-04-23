package com.toxicrain.rainengine.core.registries.tiles;

import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.json.MapInfoParser;

//TODO I dont like this class, if we are gonna handle collisions with should fully do it here -strubium
public class Collisions {

    public static boolean isCollidingBelow(AABB colideeAABB, AABB coliderAABB){
        return (colideeAABB.minY < coliderAABB.maxY && colideeAABB.maxY > coliderAABB.maxY);
    }
    public static boolean isCollidingAbove(AABB colideeAABB, AABB coliderAABB){
        return (colideeAABB.maxY > coliderAABB.minY && colideeAABB.minY < coliderAABB.minY);
    }
    public static boolean isCollidingLeft(AABB colideeAABB, AABB coliderAABB){
        return (colideeAABB.minX < coliderAABB.maxX && colideeAABB.maxX > coliderAABB.maxX);
    }
    public static boolean isCollidingRight(AABB colideeAABB, AABB coliderAABB){
        return (colideeAABB.maxX > coliderAABB.minX && colideeAABB.minX < coliderAABB.minX);
    }


    public static char collideWorld(AABB colideeAABB) {
        for (int i = Tile.aabbs.size() - 1; i >= 0; i--) {
            AABB tileAABB = Tile.aabbs.get(i);

            // Check for intersection with the player's AABB
            if (colideeAABB.intersects(tileAABB)) {
                // Handle different collision types based on map data type
                char mapType = Tile.mapDataType.get(i);

                // Check if the tile is in the list of collidable types
                boolean isCollidable = false;
                for (char collidableType : MapInfoParser.doCollide) {
                    if (mapType == collidableType) {
                        isCollidable = true;
                        break;
                    }
                }

                if (isCollidable) {
                    // Push player back slightly based on collision direction
                    if (isCollidingBelow(colideeAABB, tileAABB)) {
                        // Colliding from below
                        return 'u';
                    } else if (isCollidingAbove(colideeAABB, tileAABB)) {
                        // Colliding from above
                        return 'd';
                    }

                    if (isCollidingLeft(colideeAABB, tileAABB)) {
                        // Colliding from the left
                        return 'l';
                    } else if (isCollidingRight(colideeAABB, tileAABB)) {
                        // Colliding from the right
                        return 'r';
                    }
                }
            }
        }
        return ' ';
    }
}

