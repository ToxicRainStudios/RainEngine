package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MapInfoParser {
    public static float xpos, ypos;
    public static int xsize, ysize;
    public static int tiles = 0;
    public static ArrayList<Integer> mapDataX = new ArrayList<>();
    public static ArrayList<Integer> mapDataY = new ArrayList<>();
    public static ArrayList<Character> mapDataType = new ArrayList<>();

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
            Logger.printLOG("Number of slices: " + slices.length());
            for (int j = 0; j < slices.length(); j++) {
                JSONArray slice = slices.getJSONArray(j);
                Logger.printLOG("Processing slice " + j + " with length: " + slice.length());
                for (int k = 0; k < slice.length(); k++) {
                    String row = slice.getString(k);
                    Logger.printLOG("Processing row " + k + ": " + row);
                    // Check each character in the row
                    for (int l = 0; l < row.length(); l++) {
                        if (!(row.charAt(l) == ' ')) {
                            // Calculate coordinates (consider adjusting multiplier based on your actual map scale)
                            int xCoordinate = l;
                            int yCoordinate = k;

                            // Log position and add to data
                            Logger.printLOG("Found ':' at row " + k + ", column " + l);
                            tiles++;
                            mapDataX.add(xCoordinate * 2);
                            mapDataY.add(yCoordinate * -2);
                            mapDataType.add(row.charAt(l));
                        }
                    }
                }
                System.out.println("----");
            }
        }

            // Log the final map data
            System.out.println("mapDataX: " + mapDataX);
            System.out.println("mapDataY: " + mapDataY);
        }
    }

