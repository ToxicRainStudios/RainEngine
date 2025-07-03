package com.toxicrain.rainengine.core.json;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileInfo;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.FileUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PaletteInfoParser {

    public static Map<Character, TileInfo> tileMappings = new HashMap<>();

    public static void loadTextureMappings() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/custom/palette.json", "resources/json/palette.json");

        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);
            JSONObject textureMappings = jsonObject.getJSONObject("textures");

            for (String key : textureMappings.keySet()) {
                char textureMapChar = key.charAt(0);
                JSONObject textureData = textureMappings.getJSONObject(key);

                String textureLocation = textureData.optString("name", "rainengine:missing");
                boolean collision = textureData.optBoolean("collision", false);

                Resource textureResource = new Resource(textureLocation);
                TextureInfo texture = TextureSystem.getTexture(textureResource);
                TileInfo tileInfo = new TileInfo(textureResource, texture, collision);

                RainLogger.RAIN_LOGGER.info("Loaded TileInfo: {} ({})", tileInfo.getTextureName(), textureMapChar);

                tileMappings.put(textureMapChar, tileInfo);
            }

        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Failed to load palette.json", e);
        }
    }

    public static TileInfo getTileInfo(char textureMapChar) {
        return tileMappings.getOrDefault(
                textureMapChar,
                new TileInfo(new Resource("rainengine:missing"), TextureSystem.getTexture("rainengine:missing"), false)
        );
    }

    public static TileInfo getTileInfo(String textureName) {
        for (TileInfo tileInfo : tileMappings.values()) {
            if (tileInfo.getTextureName().equals(textureName)) {
                return tileInfo;
            }
        }
        return new TileInfo(new Resource("rainengine:missing"), TextureSystem.getTexture("rainengine:missing"), false);
    }

    public static TileInfo getTileInfo(Resource textureResource) {
        for (TileInfo tileInfo : tileMappings.values()) {
            if (tileInfo.getTextureResource().equals(textureResource)) {
                return tileInfo;
            }
        }
        return new TileInfo(new Resource("rainengine:missing"), TextureSystem.getTexture("rainengine:missing"), false);
    }


    public static boolean hasCollision(char textureMapChar) {
        TileInfo tileInfo = tileMappings.get(textureMapChar);
        return tileInfo != null && tileInfo.isCollision();
    }

    public static Iterable<Character> getCollisionTiles() {
        List<Character> collisionTiles = new ArrayList<>();
        for (Map.Entry<Character, TileInfo> entry : tileMappings.entrySet()) {
            if (entry.getValue().isCollision()) {
                collisionTiles.add(entry.getKey());
            }
        }
        return collisionTiles;
    }
}
