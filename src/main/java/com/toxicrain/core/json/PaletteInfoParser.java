package com.toxicrain.core.json;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.util.FileUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;

import static com.toxicrain.util.TextureUtils.*;

public class PaletteInfoParser {

    public static JSONObject textureMappings;

    public static void loadTextureMappings() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/custom/palette.json", "resources/json/palette.json");

        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            textureMappings = jsonObject.getJSONObject("textures");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextureInfo getTexture(char textureMapChar) {
        String textureKey = String.valueOf(textureMapChar);
        String textureName = textureMappings.optString(textureKey, "missingTexture");

        // Replace this with actual logic to retrieve TextureInfo based on textureName
        return getTextureInfoByName(textureName);
    }

    private static TextureInfo getTextureInfoByName(String textureName) {
        switch (textureName) {
            case "floorTexture":
                return floorTexture;
            case "concreteTexture1":
                return concreteTexture1;
            case "concreteTexture2":
                return concreteTexture2;
            case "dirtTexture1":
                return dirtTexture1;
            case "dirtTexture2":
                return dirtTexture2;
            case "grassTexture1":
                return grassTexture1;
            case "playerTexture":
                return playerTexture;
            case "splatterTexture":
                return splatterTexture;
            default:
                return missingTexture;
        }
    }
}
