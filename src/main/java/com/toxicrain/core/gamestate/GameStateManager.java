package com.toxicrain.core.gamestate;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.MapInfoParser;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
            // Create a JSONObject from the GameState
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("playerName", gameState.playerName);
            jsonObject.put("playerX", gameState.playerX);
            jsonObject.put("playerY", gameState.playerY);
            jsonObject.put("playerHealth", gameState.playerHealth);
            // Add more fields as needed

            // Write the JSON object to a file
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonObject.toString(4)); // Indent with 4 spaces for readability
            }
        } catch (IOException e) {
            Logger.printERROR("Error saving game state: " + e.getMessage());
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

            // Extract the values and set them in the GameState object
            gameState.playerName = jsonObject.optString("playerName", "DefaultName");
            gameState.playerX = jsonObject.optInt("playerX", MapInfoParser.playerx);
            gameState.playerY = jsonObject.optInt("playerY", MapInfoParser.playery);
            gameState.playerHealth = (float) jsonObject.optDouble("playerHealth", 100.0);
            // Add more fields as needed

        } catch (IOException e) {
            Logger.printLOG("Error loading game state: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Logger.printLOG("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return gameState;
    }
}
