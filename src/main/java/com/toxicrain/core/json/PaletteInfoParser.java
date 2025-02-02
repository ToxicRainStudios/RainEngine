package com.toxicrain.core.json;

import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.FileUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;

public class PaletteInfoParser {

    public static JSONObject textureMappings;

    public static void loadTextureMappings() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/custom/palette.json", "resources/json/palette.json");

        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            textureMappings = jsonObject.getJSONObject("textures");

            // Iterate through all the texture keys and check for collision
            for (String key : textureMappings.keySet()) {
                if (hasCollision(key.charAt(0))) {
                   MapInfoParser.doCollide.add(key.charAt(0));  // Add the character to the list if it has collision
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextureInfo getTexture(char textureMapChar) {
        String textureKey = String.valueOf(textureMapChar);
        JSONObject textureData = textureMappings.optJSONObject(textureKey);

        // If the textureData is found, return the corresponding texture
        String textureName = textureData.optString("name", "missingTexture");
        return TextureSystem.getTexture(textureName);
    }

    // Method to check if a tile has collision
    public static boolean hasCollision(char textureMapChar) {
        String textureKey = String.valueOf(textureMapChar);
        JSONObject textureData = textureMappings.optJSONObject(textureKey);

        // If texture data is found, return the collision flag
        if (textureData != null) {
            return textureData.optBoolean("collision", false);  // Default to false if not present
        }

        // Default to no collision if no data is found
        return false;
    }
}
