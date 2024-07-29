package com.toxicrain.core.json;


import com.toxicrain.core.Logger;
import com.toxicrain.core.render.Tile;
import com.toxicrain.util.FileUtils;
import com.toxicrain.util.LightUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static void parseMapFile() throws IOException {
        // Read JSON file as String
        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory("resources/json/map.json"));

        doCollide.add(':');

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check for required keys
            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices") || !part.has("lighting")) {
                Logger.printERROR("Missing keys in JSON object at index " + i);
                Logger.printERROR(part.toString(4));
                continue;
            }

            playerx = part.getInt("playerx");
            playery = part.getInt("playery");

            if (doExtraLogs) {
                Logger.printLOG("type: " + part.getString("type"));
                xsize = part.getInt("xsize");
                ysize = part.getInt("ysize");
                Logger.printLOG("xsize: " + xsize);
                Logger.printLOG("ysize: " + ysize);
            }

            try {
                JSONArray slices = part.getJSONArray("slices");
                JSONArray lighting = part.getJSONArray("lighting");

                // Clear existing lighting data
                LightUtils.getLightSources().clear();

                // Process lighting data
                for (int j = 0; j < lighting.length(); j++) {
                    JSONObject lightSource = lighting.getJSONObject(j);
                    float x = (float) lightSource.getDouble("x");
                    float y = (float) lightSource.getDouble("y");
                    float strength = (float) lightSource.getDouble("strength");
                    LightUtils.addLightSource(x, y, strength);
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
                                Tile.mapDataType.add(row.charAt(l));
                                Tile.addCollision(ypos, xpos);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Logger.printERROR("Error parsing slices or lighting: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Log the final map data
        Logger.printLOGConditional("mapDataX: " + mapDataX, doExtraLogs);
        Logger.printLOGConditional("mapDataY: " + mapDataY, doExtraLogs);
        Logger.printLOGConditional("Lighting sources: " + LightUtils.getLightSources(), doExtraLogs);
    }
}