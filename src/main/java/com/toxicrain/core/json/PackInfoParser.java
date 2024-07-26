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
    public static String letterA = null;
    public static String letterB = null;
    public static String letterC = null;
    public static String letterD = null;
    public static String letterE = null;
    public static String letterF = null;
    public static String letterG = null;
    public static String letterH = null;
    public static String letterI = null;
    public static String letterJ = null;
    public static String letterK = null;
    public static String letterL = null;
    public static String letterM = null;
    public static String letterN = null;
    public static String letterO = null;
    public static String letterP = null;
    public static String letterQ = null;
    public static String letterR = null;
    public static String letterS = null;
    public static String letterT = null;
    public static String letterU = null;
    public static String letterV = null;
    public static String letterW = null;
    public static String letterX = null;
    public static String letterY = null;
    public static String letterZ = null;
    public static String letterSPACE = null;






    /**
     * Loads the pack.json and parsers it into variables
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
                            case "letterA":
                                letterA = workingDirectory + "/" + value;
                                break;
                            case "letterB":
                                letterB = workingDirectory + "/" + value;
                                break;
                            case "letterC":
                                letterC = workingDirectory + "/" + value;
                                break;
                            case "letterD":
                                letterD = workingDirectory + "/" + value;
                                break;
                            case "letterE":
                                letterE = workingDirectory + "/" + value;
                                break;
                            case "letterF":
                                letterF = workingDirectory + "/" + value;
                                break;
                            case "letterG":
                                letterG = workingDirectory + "/" + value;
                                break;
                            case "letterH":
                                letterH = workingDirectory + "/" + value;
                                break;
                            case "letterI":
                                letterI = workingDirectory + "/" + value;
                                break;
                            case "letterJ":
                                letterJ = workingDirectory + "/" + value;
                                break;
                            case "letterK":
                                letterK = workingDirectory + "/" + value;
                                break;
                            case "letterL":
                                letterL = workingDirectory + "/" + value;
                                break;
                            case "letterM":
                                letterM = workingDirectory + "/" + value;
                                break;
                            case "letterN":
                                letterN = workingDirectory + "/" + value;
                                break;
                            case "letterO":
                                letterO = workingDirectory + "/" + value;
                                break;
                            case "letterP":
                                letterP = workingDirectory + "/" + value;
                                break;
                            case "letterQ":
                                letterQ = workingDirectory + "/" + value;
                                break;
                            case "letterR":
                                letterR = workingDirectory + "/" + value;
                                break;
                            case "letterS":
                                letterS = workingDirectory + "/" + value;
                                break;
                            case "letterT":
                                letterT = workingDirectory + "/" + value;
                                break;
                            case "letterU":
                                letterU = workingDirectory + "/" + value;
                                break;
                            case "letterV":
                                letterV = workingDirectory + "/" + value;
                                break;
                            case "letterW":
                                letterW = workingDirectory + "/" + value;
                                break;
                            case "letterX":
                                letterX = workingDirectory + "/" + value;
                                break;
                                case "letterY":
                                letterY = workingDirectory + "/" + value;
                                break;
                            case "letterZ":
                                letterZ = workingDirectory + "/" + value;
                                break;
                            case "letterSPACE":
                                letterSPACE = workingDirectory + "/" + value;
                                break;



                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.printERROR("File not found: " + packLocation);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading file: " + packLocation);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
