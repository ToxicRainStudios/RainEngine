package util.json;

import org.json.*;
import java.io.IOException;

public class JSONParser {
    public static int height = 0;
    public static String color = "BLUE";
    public static int width = 0;
    public static int depth = 0;
    public static int xpos = 0;
    public static int ypos = 0;
    public static int zpos = 0;

    public static int cubeCount = 0;

    public static void load() {
            try {
                JSONObject jsonObject = JSONDataReader.readJsonFile("C:\\Users\\hudso\\OneDrive\\Desktop\\MWC\\game\\src\\main\\resources\\map.json");
                final String mapName = jsonObject.getString("mapname");
                final String mapAuthor = jsonObject.getString("mapauthor");
                final String creationDate = jsonObject.getString("creationdate");

                JSONArray brushesArray = jsonObject.getJSONArray("brushes");
                for (int i = 0; i < brushesArray.length(); i++) {
                    JSONObject brush = brushesArray.getJSONObject(i);
                    String type = brush.getString("type");
                    // Additional attributes specific to each brush type
                    if ("cube".equals(type)) {
                        setHeight(brush.getInt("height"));
                        setColor(brush.getString("color"));
                        setWidth(brush.getInt("width"));
                        setDepth(brush.getInt("depth"));
                        setXPos(brush.getInt("xposition"));
                        setYPos(brush.getInt("yposition"));
                        setZPos(brush.getInt("zposition"));
                        incrementCubeCount();
                    }
                    else if ("sphere".equals(type)) {
                        //int radius = brush.getInt("radius");
                    }
                    else if ("pyramid".equals(type)) {
                            //int scale = brush.getInt("scale");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public static void setHeight ( int height){
            JSONParser.height = height;
        }
        public static int getHeight () {
        return height;
        }
        public static void setColor ( String color){
            JSONParser.color = color;
        }
        public static String getColor () {
            return color;
        }
        public static void setWidth ( int width){
            JSONParser.width = width;
        }
        public static int getWidth () {
            return width;
        }
        public static void setDepth ( int depth){
            JSONParser.depth = depth;

        }
        public static int getDepth () {
            return depth;
        }
        public static void setXPos ( int xpos){
            JSONParser.xpos = xpos;
        }
        public static int getXPos () {
            return xpos;
        }
        public static void setYPos ( int ypos){
            JSONParser.ypos = ypos;
        }
        public static int getYPos () {
            return ypos;
        }
        public static void setZPos ( int zpos){
            JSONParser.zpos = zpos;
        }
        public static int getZPos () {
            return zpos;
        }
        public static void incrementCubeCount () {
            JSONParser.cubeCount = cubeCount + 1;
        }
        public static int getCubeCount () {
            return cubeCount;
        }
    }