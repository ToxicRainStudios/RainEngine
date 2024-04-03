package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JSON {
    public static void main(String[] args) {
        try {
            // Read the JSON file as a String
            String jsonFilePath = "resources/example.json";
            String jsonStr = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

            // Parse the JSON string
            JSONObject jsonObject = new JSONObject(jsonStr);

            // Example: Reading values from the JSON object
            String name = jsonObject.getString("name");
            int age = jsonObject.getInt("age");

            System.out.println("Name: " + name);
            System.out.println("Age: " + age);

            // Example: Accessing a nested JSON object
            JSONObject address = jsonObject.getJSONObject("address");
            String city = address.getString("city");
            String country = address.getString("country");

            System.out.println("City: " + city);
            System.out.println("Country: " + country);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
