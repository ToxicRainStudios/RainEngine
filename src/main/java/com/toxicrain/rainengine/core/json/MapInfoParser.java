package com.toxicrain.rainengine.core.json;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.eventbus.events.load.MapLoadEvent;
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

    public Vector2 mapSize = new Vector2(0, 0);
    public Vector2 playerSpawnPos = new Vector2(0, 0);
    public int tiles = 0;
    public ArrayList<TilePos> mapData = new ArrayList<>();

    public static MapInfoParser getInstance() {
        return BaseInstanceable.getInstance(MapInfoParser.class);
    }

    private JSONArray currentMapJson;

    public JSONArray getMapJson() {
        return currentMapJson;
    }

    public void parseMapFile(String mapName) throws IOException {
        // Clear all previous data for a fresh load
        mapSize = new Vector2(0, 0);
        playerSpawnPos = new Vector2(0, 0);
        tiles = 0;
        mapData.clear();
        Tile.mapDataType.clear();
        Tile.clearCollision();
        LightSystem.getLightSources().clear();

        parseMap(mapName, 0, 0, true);
    }

    private void parseMap(String mapName, int offsetX, int offsetY, boolean isMainMap) throws IOException {
        LuaManager.executeMapScript(mapName);

        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.MAP_PATH + mapName + ".json"));
        currentMapJson = new JSONArray(jsonString);
        JSONArray jsonArray = currentMapJson;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices") || !part.has("lighting")) {
                RainLogger.RAIN_LOGGER.error("Missing keys in JSON object at index {}", i);
                RainLogger.RAIN_LOGGER.error(part.toString(4));
                continue;
            }

            if (!part.getString("type").equals("map")) {
                throw new IllegalStateException("Map: '" + mapName + "' was loaded without the map type!");
            }

            // Only update map size and player spawn for the main map
            if (isMainMap) {
                mapSize.x = part.getInt("xsize");
                mapSize.y = part.getInt("ysize");
                playerSpawnPos.x = part.getInt("playerx");
                playerSpawnPos.y = part.getInt("playery");
            }

            try {
                JSONArray slices = part.getJSONArray("slices");
                JSONArray lighting = part.getJSONArray("lighting");

                // Process lighting with offset
                for (int j = 0; j < lighting.length(); j++) {
                    JSONObject lightSource = lighting.getJSONObject(j);
                    float x = (float) lightSource.getDouble("x") + offsetX * 2;
                    float y = (float) lightSource.getDouble("y") + offsetY * -2;
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
                                int tileX = l + offsetX;
                                int tileY = k + offsetY;

                                mapData.add(new TilePos(tileX * 2, tileY * -2, 0.0001f));
                                tiles++;
                                Tile.mapDataType.add(tileChar);

                                if (PaletteInfoParser.getInstance().hasCollision(tileChar)) {
                                    Tile.addCollision(tileX, tileY);
                                }
                            }
                        }
                    }
                }

                // Recursively load submaps
                if (part.has("subMaps")) {
                    JSONArray subMaps = part.getJSONArray("subMaps");
                    for (int subMapIndex = 0; subMapIndex < subMaps.length(); subMapIndex++) {
                        JSONObject subMap = subMaps.getJSONObject(subMapIndex);
                        String subMapName = subMap.getString("name");
                        if (!Objects.equals(subMapName, mapName)) {
                            int subMapOffsetX = subMap.getInt("offsetX");
                            int subMapOffsetY = subMap.getInt("offsetY");

                            parseMap(subMapName, offsetX + subMapOffsetX, offsetY + subMapOffsetY, false);
                        } else {
                            RainLogger.RAIN_LOGGER.error("Submap name :{} matches current map name: {}", subMapName, mapName);
                            return;
                        }
                    }
                }

                SmeagleBus.getInstance().post(new MapLoadEvent(mapName, tiles, playerSpawnPos));


            } catch (JSONException e) {
                RainLogger.RAIN_LOGGER.error("Error parsing map data: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
