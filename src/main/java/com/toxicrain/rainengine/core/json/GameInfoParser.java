package com.toxicrain.rainengine.core.json;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.logging.RainLogger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import com.toxicrain.rainengine.util.FileUtils;

/**
 * GameInfoParser parsers the gameinfo.json file
 * needed for game functionality
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameInfoParser extends BaseInstanceable<GameInfoParser> {
    public String defaultWindowName = null;
    public String engineVersion = null;
    public String gameMainClass = null;
    public String gameName = null;
    public String gameMakers = null;
    public String gameVersion = null;
    public String gameWebsite = null;
    public float playerSize= 0.0f;
    public int maxTexturesPerBatch = 100; //Safety, don't crash if we forget to add this to gameinfo.json
    public int minZoom = 3;
    public int maxZoom = 25;

    public static GameInfoParser getInstance() {
        return BaseInstanceable.getInstance(GameInfoParser.class);
    }

    /**
     * Loads the gameinfo.json and parsers it into variables
     */
    public void loadGameInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.GAMEINFO_PATH);

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
                        switch (key) {
                            case "defaultWindowName":
                                defaultWindowName = value;
                                break;
                            case "engineVersion":
                                engineVersion = value;
                                break;
                            case "gameName":
                                gameName = value;
                                break;
                            case "gameMakers":
                                gameMakers = value;
                                break;
                            case "gameVersion":
                                gameVersion = value;
                                break;
                            case "gameWebsite":
                                gameWebsite = value;
                                break;
                            case "gameMainClass":
                                gameMainClass = value;
                                break;
                            case "playerSize":
                                playerSize = Float.parseFloat(value) / 10;
                                break;
                            case "maxTexturesPerBatch":
                                maxTexturesPerBatch = Integer.parseInt(value);
                                break;
                            case "minZoom":
                                minZoom = Integer.parseInt(value);
                                break;
                            case "maxZoom":
                                maxZoom = Integer.parseInt(value);
                                break;
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            RainLogger.RAIN_LOGGER.error("File not found: {}", filePath);
        }
        catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Error reading file: {}", filePath);
            e.printStackTrace();
        } catch (Exception e) {
            RainLogger.RAIN_LOGGER.error("Error parsing JSON: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
