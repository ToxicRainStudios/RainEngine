package com.toxicrain.core.json;

import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class PackInfoParser {
    public static String playerTexture = null;
    public static String floorTexture = null;
    public static String splatterTexture = null;
    public static String concreteTexture1 = null;
    public static String concreteTexture2 = null;
    public static String missingTexture = null;

    /**
     * Loads the pack.json and parsers it into variables
     * @param filePath The pack.json file path to parse
     */
    public static void loadPackInfo(String filePath) {
        String workingDirectory = FileUtils.getCurrentWorkingDirectory("resources/images/");


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
                        if (key.equals("playerTexture")) {
                            playerTexture = workingDirectory + "/" + value;
                        }
                        if (key.equals("floorTexture")) {
                            floorTexture = workingDirectory + "/" + value;
                        }
                        if (key.equals("splatterTexture")) {
                            splatterTexture = workingDirectory + "/" + value;
                        }
                        if (key.equals("concreteTexture1")) {
                            concreteTexture1 = workingDirectory + "/" + value;
                        }
                        if (key.equals("concreteTexture2")) {
                            concreteTexture2 = workingDirectory + "/" + value;
                        }
                        if (key.equals("missingTexture")) {
                            missingTexture = workingDirectory + "/" + value;
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
