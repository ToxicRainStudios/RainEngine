package com.toxicrain.core.json;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Iterator;

import com.toxicrain.util.FileUtils;

/**
 * GameInfoParser parsers the gameinfo.json file
 * needed for game functionality
 */
public class GameInfoParser {
    public static String defaultWindowName = null;
    public static String engineVersion = null;
    public static String gameName = null;
    public static String gameMakers = null;
    public static String gameVersion = null;
    public static String gameWebsite = null;
    public static int maxTexturesPerBatch = 100; //Safety, don't crash if we forget to add this to gameinfo.json
    public static int minZoom = 3;
    public static int maxZoom = 25;

    /**
     * Loads the gameinfo.json and parsers it into variables
     */
    public static void loadGameInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/gameinfo.json");

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
                        if (key.equals("defaultWindowName")) {
                            defaultWindowName = value;
                        }
                        else if (key.equals("engineVersion")) {
                            engineVersion = value;
                        }
                        else if (key.equals("gameName")) {
                            gameName = value;
                        }
                        else if (key.equals("gameMakers")) {
                            gameMakers = value;
                        }
                        else if (key.equals("gameVersion")) {
                            gameVersion = value;
                        }
                        else if (key.equals("gameWebsite")) {
                            gameWebsite = value;
                        }
                        else if (key.equals("maxTexturesPerBatch")) {
                            maxTexturesPerBatch = Integer.parseInt(value);
                        }
                        else if (key.equals("minZoom")) {
                            minZoom = Integer.parseInt(value);
                        }
                        else if (key.equals("maxZoom")) {
                            maxZoom = Integer.parseInt(value);
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
