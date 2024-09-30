package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class PackInfoParser {
    public static String playerTexture = null;
    public static String floorTexture = null;
    public static String splatterTexture = null;
    public static String concreteTexture1 = null;
    public static String concreteTexture2 = null;
    public static String missingTexture = null;
    public static String dirtTexture1 = null;
    public static String grassTexture1 = null;
    public static String dirtTexture2 = null;


    /**
     * Loads the pack.json and parsers it into variables
     *
     */
    public static void loadPackInfo() {
        String packLocation = FileUtils.getCurrentWorkingDirectory("resources/custom/pack.json", "resources/json/pack.json");
        String workingDirectory = FileUtils.getCurrentWorkingDirectory("resources/images/");

        try {
            // Read the file content into a string
            String jsonString = FileUtils.readFile(packLocation);

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
                            case "playerTexture":
                                playerTexture = workingDirectory + "/" + value;
                                break;
                            case "floorTexture":
                                floorTexture = workingDirectory + "/" + value;
                                break;
                            case "splatterTexture":
                                splatterTexture = workingDirectory + "/" + value;
                                break;
                            case "concreteTexture1":
                                concreteTexture1 = workingDirectory + "/" + value;
                                break;
                            case "concreteTexture2":
                                concreteTexture2 = workingDirectory + "/" + value;
                                break;
                            case "dirtTexture1":
                                dirtTexture1 = workingDirectory + "/" + value;
                                break;
                            case "grassTexture1":
                                grassTexture1 = workingDirectory + "/" + value;
                                break;
                            case "dirtTexture2":
                                dirtTexture2 = workingDirectory + "/" + value;
                                break;
                            case "missingTexture":
                                missingTexture = workingDirectory + "/" + value;
                                break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.printERROR("File not found: " + packLocation);
            e.printStackTrace();
        } catch (IOException e) {
            Logger.printERROR("Error reading file: " + packLocation);
            e.printStackTrace();
        } catch (Exception e) {
            Logger.printERROR("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
