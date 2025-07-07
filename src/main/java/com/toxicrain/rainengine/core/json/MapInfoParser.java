package com.toxicrain.rainengine.core.json;

import com.toxicrain.rainengine.core.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.lua.LuaManager;
import com.toxicrain.rainengine.core.registries.tiles.Tile;
import com.toxicrain.rainengine.util.FileUtils;
import com.toxicrain.rainengine.light.LightSystem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapInfoParser extends BaseInstanceable<MapInfoParser> {

    public Vector2 mapPos = new Vector2(0, 0);
    public Vector2 mapSize = new Vector2(0, 0);
    public Vector2 playerSpawnPos = new Vector2(0, 0);
    public int tiles = 0;
    public ArrayList<TilePos> mapData = new ArrayList<>();

    public static MapInfoParser getInstance() {
        return BaseInstanceable.getInstance(MapInfoParser.class);
    }

    public void parseMapFile(String mapName) throws IOException {
        parseMap(mapName, 0, 0);  // Main map at position (0, 0)
    }

    private void parseMap(String mapName, int offsetX, int offsetY) throws IOException {
        LuaManager.executeMapScript(mapName);

        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.MAP_PATH + mapName + ".json"));

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check for required keys
            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices") || !part.has("lighting")) {
                RainLogger.RAIN_LOGGER.error("Missing keys in JSON object at index {}", i);
                RainLogger.RAIN_LOGGER.error(part.toString(4));
                continue;
            }

            playerSpawnPos.x = part.getInt("playerx");
            playerSpawnPos.y = part.getInt("playery");

            if (!part.getString("type").equals("map")){
                throw new IllegalStateException("Map: '" + mapName + "' was loaded without the map type!");
            }

            mapSize.x = part.getInt("xsize");
            mapSize.y = part.getInt("ysize");

            try {
                JSONArray slices = part.getJSONArray("slices");
                JSONArray lighting = part.getJSONArray("lighting");

                // Clear existing lighting data
                LightSystem.getLightSources().clear();

                // Process lighting data
                for (int j = 0; j < lighting.length(); j++) {
                    JSONObject lightSource = lighting.getJSONObject(j);
                    float x = (float) lightSource.getDouble("x");
                    float y = (float) lightSource.getDouble("y");
                    float strength = (float) lightSource.getDouble("strength");
                    LightSystem.addLightSource(x, y, strength);
                }

                // Process slices
                for (int layer = 0; layer < slices.length(); layer++) {
                    JSONArray sliceLayer = slices.getJSONArray(layer);
                    for (int k = 0; k < sliceLayer.length(); k++) {
                        String row = sliceLayer.getString(k);
                        for (int l = 0; l < row.length(); l++) {
                            char tileChar = row.charAt(l);
                            if (tileChar != ' ') {
                                mapPos.x = l + offsetX;
                                mapPos.y = k + offsetY;

                                mapData.add(new TilePos(mapPos.x * 2, mapPos.y * -2, 0.0001f));
                                tiles++;
                                Tile.mapDataType.add(tileChar);

                                // Use PaletteInfoParser to check for collision
                                if (PaletteInfoParser.getInstance().hasCollision(tileChar)) {
                                    Tile.addCollision((int) mapPos.x, (int) mapPos.y);
                                }
                            }
                        }
                    }
                }

                // Check if there are sub-maps to load
                if (part.has("subMaps")) {
                    JSONArray subMaps = part.getJSONArray("subMaps");
                    for (int subMapIndex = 0; subMapIndex < subMaps.length(); subMapIndex++) {
                        JSONObject subMap = subMaps.getJSONObject(subMapIndex);
                        String subMapName = subMap.getString("name");
                        if (!Objects.equals(subMapName, mapName)) {
                            int subMapOffsetX = subMap.getInt("offsetX");
                            int subMapOffsetY = subMap.getInt("offsetY");

                            // Recursively load sub-maps too
                            parseMap(subMapName, offsetX + subMapOffsetX, offsetY + subMapOffsetY);
                        } else {
                            RainLogger.RAIN_LOGGER.error("Submap name :{} matches current map name: {}", subMapName, mapName);
                            return;
                        }
                    }
                }

            } catch (JSONException e) {
                RainLogger.RAIN_LOGGER.error("Error parsing map data: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
