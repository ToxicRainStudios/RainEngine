package com.toxicrain.core.render;


import com.toxicrain.core.json.MapInfoParser;

import java.util.ArrayList;

public class Tile {
    public static ArrayList<Float> extentTop = new ArrayList<>();
    public static ArrayList<Float> extentBottom = new ArrayList<>();
    public static ArrayList<Float> extentLeft = new ArrayList<>();
    public static ArrayList<Float> extentRight = new ArrayList<>();
    public static ArrayList<Float> extentCenterY = new ArrayList<>();
    public static ArrayList<Float> extentCenterX = new ArrayList<>();
    public static ArrayList<Character> mapDataType = new ArrayList<>();





    public static void addCollision(int yCoordinate, int xCoordinate){
        for(int n = MapInfoParser.doCollide.size()-1; n>=0; n--) {

                extentTop.add(((float) yCoordinate * -2) + 1.1f);
                extentBottom.add(((float) yCoordinate * -2) - 1.1f);
                extentLeft.add(((float) xCoordinate * 2) - 1.1f);
                extentRight.add(((float) xCoordinate * 2) + 1.1f);
                extentCenterY.add(((float) yCoordinate * -2));
                extentCenterX.add(((float) xCoordinate * 2));

        }



    }
}
