package util.json;

import org.json.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONDataReader {
    public static JSONObject readJsonFile(String filePath) throws IOException {
        // Read the JSON file as a String
        String jsonStr = new String(Files.readAllBytes(Paths.get(filePath)));

        // Parse the JSON string
        return new JSONObject(jsonStr);
    }
}
