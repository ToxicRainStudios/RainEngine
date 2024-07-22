package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provides functionality to parse map information from a JSON file.
 */
public class MapInfoParser {
    public static float xpos, ypos;
    public static int xsize, ysize;
    public static int tiles = 0;
    public static ArrayList<Integer> mapDataX = new ArrayList<>();
    public static ArrayList<Integer> mapDataY = new ArrayList<>();

    public static void parseMapFile() throws IOException {
        // Read JSON file as String
        String jsonString = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory("resources/json/map.json"));

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        // Loop through each part in the JSON array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check if required keys are present
            if (!part.has("type") || !part.has("xsize") || !part.has("ysize") || !part.has("slices")) {
                System.out.println("Missing keys in JSON object at index " + i);
                System.out.println(part.toString(4)); // Print the JSON object with missing keys for debugging
                continue;
            }

            String type = part.getString("type");
            xsize = part.getInt("xsize");
            ysize = part.getInt("ysize");

            // Print part details
            System.out.println("type: " + type);
            System.out.println("xsize: " + xsize);
            System.out.println("ysize: " + ysize);

            // Get slices
            JSONArray slices = part.getJSONArray("slices");
            for (int j = 0; j < slices.length(); j++) {
                JSONArray slice = slices.getJSONArray(j);
                for (int k = 0; k < slice.length(); k++) {
                    String row = slice.getString(k);
                    // Check each character in the row
                    for (int l = 0; l < row.length(); l++) {
                        if (row.charAt(l) == ':') {
                            Logger.printLOG("HIIII");
                            tiles++;
                            mapDataX.add(l * 2);
                            mapDataY.add(k * 2);
                        }
                    }
                }
                System.out.println("----");
            }
        }
    }
}