package com.toxicrain.rainengine.core.registries.tiles;

import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.json.PaletteInfoParser;

//TODO I dont like this class, if we are gonna handle collisions with should fully do it here -strubium
public class Collisions {

    public static boolean isCollidingBelow(AABB colideeAABB, AABB coliderAABB) {
        return (colideeAABB.minY < coliderAABB.maxY && colideeAABB.maxY > coliderAABB.maxY);
    }

    public static boolean isCollidingAbove(AABB colideeAABB, AABB coliderAABB) {
        return (colideeAABB.maxY > coliderAABB.minY && colideeAABB.minY < coliderAABB.minY);
    }

    public static boolean isCollidingLeft(AABB colideeAABB, AABB coliderAABB) {
        return (colideeAABB.minX < coliderAABB.maxX && colideeAABB.maxX > coliderAABB.maxX);
    }

    public static boolean isCollidingRight(AABB colideeAABB, AABB coliderAABB) {
        return (colideeAABB.maxX > coliderAABB.minX && colideeAABB.minX < coliderAABB.minX);
    }

    public static char collideWorld(AABB colideeAABB) {
        for (int i = Tile.aabbs.size() - 1; i >= 0; i--) {
            AABB tileAABB = Tile.aabbs.get(i);

            if (colideeAABB.intersects(tileAABB)) {
                char mapType = Tile.mapDataType.get(i);

                // Directly check if this tile type is collidable
                if (PaletteInfoParser.getInstance().hasCollision(mapType)) {
                    if (isCollidingBelow(colideeAABB, tileAABB)) {
                        return 'u';
                    } else if (isCollidingAbove(colideeAABB, tileAABB)) {
                        return 'd';
                    }

                    if (isCollidingLeft(colideeAABB, tileAABB)) {
                        return 'l';
                    } else if (isCollidingRight(colideeAABB, tileAABB)) {
                        return 'r';
                    }
                }
            }
        }
        return ' ';
    }
}
