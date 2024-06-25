package com.toxicrain.core.json;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class gameinfoParser {
    public static String defaultWindowName = null;
    public static String engineVersion = null;
    public static String gameName = null;
    public static String gameMakers = null;
    public static String gameVersion = null;
    public static String gameWebsite = null;
    public static void loadGameInfo() {
        String filePath = "C:\\Users\\hudso\\OneDrive\\Desktop\\exampleEngine.json"; // TODO Replace with the actual file path

        try {
            // Read the file content into a string
            String jsonString = readFile(filePath);

            // Parse the JSON string into a JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            // Iterate through the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.getString("type");
                System.out.println("Type: " + type);



                // Get the values array
                JSONArray valuesArray = jsonObject.getJSONArray("values");
                for (int j = 0; j < valuesArray.length(); j++) {
                    JSONObject valueObject = valuesArray.getJSONObject(j);

                    // Use traditional for-each loop instead of lambda
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);
                        System.out.println(key + ": " + valueObject.get(key));
                        if (key.equals("defaultWindowName")) {
                            defaultWindowName = value;
                        }
                        if (key.equals("engineVersion")) {
                            engineVersion = value;
                        }
                        if (key.equals("gameName")) {
                            gameName = value;
                        }
                        if (key.equals("gameMakers")) {
                            gameMakers = value;
                        }
                        if (key.equals("gameVersion")) {
                            gameVersion = value;
                        }
                        if (key.equals("gameWebsite")) {
                            gameWebsite = value;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to read the file content into a string*
     * @return contentBuilder.toString()
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine);
            }
        }
        return contentBuilder.toString();
    }
}
