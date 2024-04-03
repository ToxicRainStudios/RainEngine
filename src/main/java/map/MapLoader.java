package map;

import util.Color;

import geometry.Cube;
import util.json.JSONParser;


public class MapLoader {
    public static void LoadMap() {
        new JSONParser().load();
        for (int i = 0; i < JSONParser.cubeCount; i++) {
            Cube.drawCube(Color.BLUE, JSONParser.getWidth(), JSONParser.getHeight(), JSONParser.getDepth(), 0, 0, -5);
        }
}
}