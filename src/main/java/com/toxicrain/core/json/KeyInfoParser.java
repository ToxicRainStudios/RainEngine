package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import static org.lwjgl.glfw.GLFW.*;

/**
 * KeyInfoParser parsers the keybinds.json file
 * needed for game functionality
 */
public class KeyInfoParser {
    public static String keySprint = null;
    public static String keyWalkForward = null;
    public static String keyWalkBackward = null;
    public static String keyWalkLeft = null;
    public static String keyWalkRight = null;

    /**
     * Loads the keybinds.json and parsers it into variables
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

                    // Use traditional for-each loop instead of lambda
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);
                        switch (key) {
                            case "keySprint":
                                keySprint = value;
                                break;
                            case "keyWalkForward":
                                keyWalkForward = value;
                                break;
                            case "keyWalkBackward":
                                keyWalkBackward = value;
                                break;
                            case "keyWalkLeft":
                                keyWalkLeft = value;
                                break;
                            case "keyWalkRight":
                                keyWalkRight = value;
                                break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.printERROR("File not found: " + filePath);
            e.printStackTrace();
        }
        catch (IOException e) {
            Logger.printERROR("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            Logger.printERROR("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int convertToGLFWBind(String bind){
        switch (bind) {
            case "key_left_shift":
                return GLFW_KEY_LEFT_SHIFT;
            case "key_a":
                return GLFW_KEY_A;
            case "key_s":
                return GLFW_KEY_S;
            case "key_w":
                return GLFW_KEY_W;
            case "key_d":
                return GLFW_KEY_D;
            default:
                return GLFW_KEY_D;
        }
    }


}
