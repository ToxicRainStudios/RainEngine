package map;

import util.Color;
import org.json.*;
import geometry.Brushes;
import util.json.JSONParser;

public class MapLoader {
    public static void LoadMap() {
        JSONParser.load();
            Brushes.drawCube(
                        Color.from(JSONParser.getColor()),
                        JSONParser.getWidth(),
                        JSONParser.getHeight(),
                        JSONParser.getDepth(),
                        JSONParser.getXPos(),
                        JSONParser.getYPos(),
                        JSONParser.getZPos()
                );
        }
    }