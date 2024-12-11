package com.toxicrain.core.json;


import com.toxicrain.core.RainLogger;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.artifacts.Tile;
import com.toxicrain.util.FileUtils;
import com.toxicrain.light.LightSystem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MapInfoParser {

    public static final ArrayList<Character> doCollide = new ArrayList<>();
    public static boolean doExtraLogs = false;
    public static int xpos, ypos;
    public static int xsize, ysize;
    public static int playerx;
    public static int playery;
    public static int tiles = 0;
    public static ArrayList<Integer> mapDataX = new ArrayList<>();
    public static ArrayList<Integer> mapDataY = new ArrayList<>();
    public static ArrayList<Double> mapDataZ = new ArrayList<>();

    public static void parseMapFile(String mapName) throws IOException {
        LuaManager.executeMapScript(mapName);
        // Read JSON file as String
        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory("resources/json/" + mapName + ".json"));

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check for required keys
            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices") || !part.has("lighting")) {
                RainLogger.printERROR("Missing keys in JSON object at index " + i);
                RainLogger.printERROR(part.toString(4));
                continue;
            }

            playerx = part.getInt("playerx");
            playery = part.getInt("playery");

            if (doExtraLogs) {
                RainLogger.printLOG("type: " + part.getString("type"));
                xsize = part.getInt("xsize");
                ysize = part.getInt("ysize");
                RainLogger.printLOG("xsize: " + xsize);
                RainLogger.printLOG("ysize: " + ysize);
            }

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
                            if (row.charAt(l) != ' ') {
                                xpos = l;
                                ypos = k;

                                // Add tile data
                                mapDataX.add(xpos * 2);
                                mapDataY.add(ypos * -2);
                                mapDataZ.add(0.0001);
                                tiles++;
                                Tile.mapDataType.add(row.charAt(l));
                                Tile.addCollision(ypos, xpos);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                RainLogger.printERROR("Error parsing slices or lighting: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Log the final map data
        RainLogger.printLOGConditional("mapDataX: " + mapDataX, doExtraLogs);
        RainLogger.printLOGConditional("mapDataY: " + mapDataY, doExtraLogs);
        RainLogger.printLOGConditional("Lighting sources: " + LightSystem.getLightSources(), doExtraLogs);
    }
}