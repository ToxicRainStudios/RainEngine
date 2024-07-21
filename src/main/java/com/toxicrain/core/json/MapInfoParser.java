package com.toxicrain.core.json;

import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


/**
 * This class provides functionality to parse map information from a JSON file.
 */
/*public class MapInfoParser {
    public static void parseMapFile() throws IOException {
        // Read JSON file as String
        String jsonString = FileUtils.readFile("C:/Users/hudso/OneDrive/Desktop/MWC/game2d/json/map.json");

        // Parse JSON string
        JSONArray jsonArray = new JSONArray(jsonString);

        // Loop through each part in the JSON array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject part = jsonArray.getJSONObject(i);

            // Check if required keys are present
            if (!part.has("type") || !part.has("xsize") || !part.has("zsize") || !part.has("slices")) {
                System.out.println("Missing keys in JSON object at index " + i);
                System.out.println(part.toString(4)); // Print the JSON object with missing keys for debugging
                continue;
            }

            String type = part.getString("type");
            int xsize = part.getInt("xsize");
            int zsize = part.getInt("zsize");

            // Print part details
            System.out.println("type: " + type);
            System.out.println("zsize: " + xsize);
            System.out.println("zsize: " + zsize);

            // Get slices
            JSONArray slices = part.getJSONArray("slices");
            for (int j = 0; j < slices.length(); j++) {
                JSONArray slice = slices.getJSONArray(j);
                for (int k = 0; k < slice.length(); k++) {
                    String row = slice.getString(k);
                    System.out.println(row);
                }
                System.out.println("----");
            }
        }
    }

 */
