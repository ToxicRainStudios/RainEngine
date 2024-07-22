package com.toxicrain.core.json;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Iterator;

import com.toxicrain.util.FileUtils;

/**
 * SettingsInfoParser parsers the settings.json file
 * needed for game functionality
 */
public class SettingsInfoParser {
    public static boolean vSync = true;
    public static float windowWidth = 1920;
    public static float windowHeight = 1080;
    public static float fov = 90f;

    /**
     * Loads the settings.json and parsers it into variables
     */
    public static void loadSettingsInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/settings.json");

        try {
            // Read the file content into a string
            String jsonString = FileUtils.readFile(filePath);

            // Parse the JSON string into a JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            // Iterate through the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Get the values array
                JSONArray valuesArray = jsonObject.getJSONArray("values");
                for (int j = 0; j < valuesArray.length(); j++) {
                    JSONObject valueObject = valuesArray.getJSONObject(j);

                    // Use traditional for-each loop instead of lambda
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);
                        if (key.equals("vSync")) {
                            vSync = Boolean.parseBoolean(value);
                        }
                        else if (key.equals("windowWidth")) {
                            windowWidth = Float.parseFloat(value);
                        }
                        else if (key.equals("windowHeight")) {
                            windowHeight = Float.parseFloat(value);
                        }
                        else if (key.equals("fov")) {
                            fov = Float.parseFloat(value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
