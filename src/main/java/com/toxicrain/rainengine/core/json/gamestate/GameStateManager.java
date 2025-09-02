package com.toxicrain.rainengine.core.json.gamestate;

import com.toxicrain.rainengine.core.logging.RainLogger;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * GameStateManager handles saving and loading of game state data.
 */
public class GameStateManager {

    /**
     * Saves the given GameState to a JSON file.
     *
     * @param gameState The GameState object to save.
     * @param filePath  The path to the file where the game state will be saved.
     */
    public static void saveGameState(GameState gameState, String filePath) {
        try {
            // Dump all fields into a JSONObject
            JSONObject jsonObject = new JSONObject(gameState.getAllFields());

            // Write the JSON object to a file
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonObject.toString(4));
            }
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Error saving game state: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads a GameState from a JSON file.
     *
     * @param filePath The path to the file from which the game state will be loaded.
     * @return The loaded GameState object.
     */
    public static GameState loadGameState(String filePath) {
        GameState gameState = new GameState();
        try {
            // Read the JSON file into a string
            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
            }

            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(jsonString.toString());

            // Load all JSON key/value pairs into the GameState fields map
            Map<String, Object> loadedFields = new HashMap<>();
            for (String key : jsonObject.keySet()) {
                loadedFields.put(key, jsonObject.get(key));
            }

            gameState.setAllFields(loadedFields);

        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.info("Error loading game state: {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            RainLogger.RAIN_LOGGER.info("Error parsing JSON: {}", e.getMessage());
            e.printStackTrace();
        }
        return gameState;
    }
}
