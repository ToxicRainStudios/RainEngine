package com.toxicrain.core.json;


import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.toxicrain.core.render.Tile;

import java.io.IOException;
import java.util.ArrayList;


public class MapInfoParser {


    public static final ArrayList<Character> doCollide = new ArrayList<>();





    public static boolean doExtraLogs = false;
    public static int xpos, ypos;
    public static int xsize, ysize;
    public static int tiles = 0;
    public static ArrayList<Integer> mapDataX = new ArrayList<>();
    public static ArrayList<Integer> mapDataY = new ArrayList<>();

    public static void parseMapFile() throws IOException {
        // Read JSON file as String
        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory("resources/json/map.json"));

        doCollide.add(':');
        //doCollide.add('1');

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        // Loop through each part in the JSON array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check if required keys are present
            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices")) {
                Logger.printERROR("Missing keys in JSON object at index " + i);
                Logger.printERROR(part.toString(4)); // Print the JSON object with missing keys for debugging
                continue;
            }
            if(doExtraLogs) {
                String type = part.getString("type");
                xsize = part.getInt("xsize");
                ysize = part.getInt("ysize");

                // Print part details
                Logger.printLOG("type: " + type);
                Logger.printLOG("xsize: " + xsize);
                Logger.printLOG("ysize: " + ysize);
            }
            // Get slices
            JSONArray slices = part.getJSONArray("slices");
            Logger.printLOGConditional("Number of slices: " + slices.length(), doExtraLogs);
            for (int j = 0; j < slices.length(); j++) {
                JSONArray slice = slices.getJSONArray(j);
                Logger.printLOGConditional("Processing slice " + j + " with length: " + slice.length(), doExtraLogs);
                for (int k = 0; k < slice.length(); k++) {
                    String row = slice.getString(k);
                    Logger.printLOGConditional("Processing row " + k + ": " + row, doExtraLogs);
                    // Check each character in the row
                    for (int l = 0; l < row.length(); l++) {
                        if (!(row.charAt(l) == ' ')) {
                            // Calculate coordinates (consider adjusting multiplier based on your actual map scale)
                            xpos = l;
                            ypos = k;

                            // Log position and add to data
                            Logger.printLOGConditional("Found"+ row.charAt(l) +"at row " + k + ", column " + l, doExtraLogs);
                            tiles++;
                            mapDataX.add(xpos * 2);
                            mapDataY.add(ypos * -2);
                            Tile.mapDataType.add(row.charAt(l));
                            Tile.addCollision(ypos, xpos);
                        }
                    }
                }
                System.out.println("----");
            }
        }

            // Log the final map data
        if(doExtraLogs) {
            System.out.println("mapDataX: " + mapDataX);

            System.out.println("mapDataY: " + mapDataY);
        }
        }
    }

