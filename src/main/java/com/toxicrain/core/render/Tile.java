package com.toxicrain.core.render;

import com.toxicrain.core.json.MapInfoParser;

import java.util.ArrayList;

/**
 * Represents a tile in the game world with collision detection capabilities.
 * This class holds static lists for collision extents and map data types.
 *
 * @author Gabefry
 */
public class Tile {
    /**
     * List of top extents for tile collisions.
     */
    public static ArrayList<Float> extentTop = new ArrayList<>();

    /**
     * List of bottom extents for tile collisions.
     */
    public static ArrayList<Float> extentBottom = new ArrayList<>();

    /**
     * List of left extents for tile collisions.
     */
    public static ArrayList<Float> extentLeft = new ArrayList<>();

    /**
     * List of right extents for tile collisions.
     */
    public static ArrayList<Float> extentRight = new ArrayList<>();

    /**
     * List of center Y coordinates for tile collisions.
     */
    public static ArrayList<Float> extentCenterY = new ArrayList<>();

    /**
     * List of center X coordinates for tile collisions.
     */
    public static ArrayList<Float> extentCenterX = new ArrayList<>();
    public static ArrayList<Character> mapDataType = new ArrayList<>();

    /**
     * Adds collision information for a tile at specified coordinates.
     *
     * @param yCoordinate The Y coordinate of the tile.
     * @param xCoordinate The X coordinate of the tile.
     */
    public static void addCollision(int yCoordinate, int xCoordinate){
        for(int n = MapInfoParser.doCollide.size()-1; n >= 0; n--) {
            extentTop.add(((float) yCoordinate * -2) + 1.1f);
            extentBottom.add(((float) yCoordinate * -2) - 1.1f);
            extentLeft.add(((float) xCoordinate * 2) - 1.1f);
            extentRight.add(((float) xCoordinate * 2) + 1.1f);
            extentCenterY.add(((float) yCoordinate * -2));
            extentCenterX.add(((float) xCoordinate * 2));
        }
    }
}
