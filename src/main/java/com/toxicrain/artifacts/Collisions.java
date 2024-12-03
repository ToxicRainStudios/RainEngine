package com.toxicrain.artifacts;

import com.toxicrain.core.json.MapInfoParser;

import java.util.BitSet;


public class Collisions {


    public Collisions(float minX, float minY, float maxX, float maxY) {

    }

    // Iterate through all tile AABBs
    public static char collide(AABB colideeAABB, int i) {

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
                if (colideeAABB.minY < tileAABB.maxY && colideeAABB.maxY > tileAABB.maxY) {
                    // Colliding from below
                    return 'u';
                } else if (colideeAABB.maxY > tileAABB.minY && colideeAABB.minY < tileAABB.minY) {
                    // Colliding from above
                    return 'd';
                }

                if (colideeAABB.minX < tileAABB.maxX && colideeAABB.maxX > tileAABB.maxX) {
                    // Colliding from the left
                    return  'l';
                } else if (colideeAABB.maxX > tileAABB.minX && colideeAABB.minX < tileAABB.minX) {
                    // Colliding from the right
                    return 'r';
                }
            }

            // Handle special collision types (e.g., type '1')
            if (mapType == '1') {
                //implement later omg
            }
        }
        return ' ';
    }
}

