package com.toxicrain.core.render;


import com.toxicrain.core.json.MapInfoParser;

public class Tile {






    public static void addColision(char toEvaluate, int yCoordinate, int xCoordinate){
        for(int n = MapInfoParser.doCollide.size()-1; n>=0; n--) {
            if (toEvaluate==MapInfoParser.doCollide.get(n)) {
                MapInfoParser.extentTop.add(((float) yCoordinate * -2) + 1.1f);
                MapInfoParser.extentBottom.add(((float) yCoordinate * -2) - 1.1f);
                MapInfoParser.extentLeft.add(((float) xCoordinate * 2) - 1.1f);
                MapInfoParser.extentRight.add(((float) xCoordinate * 2) + 1.1f);
                MapInfoParser.extentCenterY.add(((float) yCoordinate * -2));
                MapInfoParser.extentCenterX.add(((float) xCoordinate * 2));
            }
        }



    }
}
