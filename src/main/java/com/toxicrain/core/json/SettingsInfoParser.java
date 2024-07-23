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

    private static JSONArray jsonArray = new JSONArray();
    private static JSONObject valueObject = new JSONObject();

    /**
     * Loads the settings.json and parsers it into variables
     */
    public static void loadSettingsInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/settings.json");

        try {
            // Read the file content into a string
            String jsonString = FileUtils.readFile(filePath);

            // Parse the JSON string into a JSONArray
            jsonArray = new JSONArray(jsonString);

            // Iterate through the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Get the values array
                JSONArray valuesArray = jsonObject.getJSONArray("values");
                for (int j = 0; j < valuesArray.length(); j++) {
                    valueObject = valuesArray.getJSONObject(j);

                    // Use traditional for-each loop instead of lambda
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);
                        switch (key) {
                            case "vSync":
                                vSync = Boolean.parseBoolean(value);
                                break;
                            case "windowWidth":
                                windowWidth = Float.parseFloat(value);
                                break;
                            case "windowHeight":
                                windowHeight = Float.parseFloat(value);
                                break;
                            case "fov":
                                fov = Float.parseFloat(value);
                                break;
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

    public static void modifyKey(String key, String newValue) {
        if (valueObject == null) {
            System.err.println("Error: valueObject is null");
            return;
        }

        valueObject.put(key, newValue);
        String updatedJsonString = jsonArray.toString();

        try {
            FileUtils.writeFile(FileUtils.getCurrentWorkingDirectory("resources/json/settings.json"), updatedJsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}