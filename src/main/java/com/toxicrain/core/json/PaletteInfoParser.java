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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TextureInfo getTexture(char textureMapChar) {
        String textureKey = String.valueOf(textureMapChar);
        String textureName = textureMappings.optString(textureKey, "missingTexture");

        return TextureSystem.getTexture(textureName);
    }
}
