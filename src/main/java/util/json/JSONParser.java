package util.json;

import org.json.*;
import java.io.IOException;

public class JSONParser {
    public static int height = 0;
    public static int width = 0;
    public static int depth = 0;
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
                    switch (type) {
                        case "cube":
                            setHeight(brush.getInt("height"));
                            setWidth(brush.getInt("width"));
                            setDepth(brush.getInt("depth"));
                            //int rotation = brush.getInt("rotation");
                            cubeCount++;
                            break;
                        case "sphere":
                            //int radius = brush.getInt("radius");
                            break;
                        case "pyramid":
                            //int scale = brush.getInt("scale");
                            break;
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
    }