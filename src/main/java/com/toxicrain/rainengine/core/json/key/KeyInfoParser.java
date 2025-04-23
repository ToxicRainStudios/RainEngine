package com.toxicrain.rainengine.core.json.key;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.toxicrain.rainengine.core.RainLogger;
import com.toxicrain.rainengine.util.FileUtils;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

/**
 * KeyInfoParser parsers the keybinds.json file
 * needed for game functionality
 */
public class KeyInfoParser {
    // A Map to hold the key bindings
    @Getter
    private static final Map<String, String> keyBindings = new HashMap<>();

    /**
     * Loads the keybinds.json and parses it into the {@link HashMap}
     */
    public static void loadKeyInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/keybinds.json");

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

                    // Use traditional for-each loop to get keys and values
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);

                        // Dynamically add the key-value pairs to the map
                        keyBindings.put(key, value);
                    }
                }
            }

            RainLogger.RAIN_LOGGER.info("Key bindings loaded successfully.");

        }
        catch (FileNotFoundException e) {
            RainLogger.RAIN_LOGGER.error("File not found: {}", filePath);
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Error reading file: {}", filePath);
            e.printStackTrace();
        } catch (Exception e) {
            RainLogger.RAIN_LOGGER.error("Error parsing JSON: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates a keybinding in memory and writes the change to keybinds.json
     *
     * @param key   The keybinding to update
     * @param value The new value for the keybinding
     */
    public static void updateKeyBinding(String key, String value) {
        if (keyBindings.containsKey(key)) {
            keyBindings.put(key, value);
            saveKeyBindings();
            RainLogger.RAIN_LOGGER.info("Key binding updated: {} -> {}", key, value);
        } else {
            RainLogger.RAIN_LOGGER.error("Key binding not found: {}", key);
        }
    }

    /**
     * Saves the current key bindings to keybinds.json
     */
    private static void saveKeyBindings() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/keybinds.json");

        JSONArray jsonArray = new JSONArray();
        JSONObject keyBindingsObject = new JSONObject();
        JSONArray valuesArray = new JSONArray();

        for (Map.Entry<String, String> entry : keyBindings.entrySet()) {
            JSONObject valueObject = new JSONObject();
            valueObject.put(entry.getKey(), entry.getValue());
            valuesArray.put(valueObject);
        }

        keyBindingsObject.put("values", valuesArray);
        jsonArray.put(keyBindingsObject);

        try {
            FileUtils.writeFile(filePath, jsonArray.toString(4)); // Pretty print with indentation
            RainLogger.RAIN_LOGGER.info("Key bindings saved successfully.");
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Error writing file: {}", filePath);
            e.printStackTrace();
        }
    }


    /**
     * Gets a keybinding from keybinds.json
     *
     * @param key The keybinding to get
     */
    public static String getKeyBind(String key) {
        return keyBindings.getOrDefault(key, "undefined");
    }

}
