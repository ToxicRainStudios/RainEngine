package com.toxicrain.rainengine.core.json;

import com.toxicrain.rainengine.core.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.datatypes.TileInfo;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.util.FileUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaletteInfoParser extends BaseInstanceable<PaletteInfoParser> {

    public static Map<Character, TileInfo> tileMappings = new HashMap<>();

    public static PaletteInfoParser getInstance() {
        return BaseInstanceable.getInstance(PaletteInfoParser.class);
    }

    public void loadTextureMappings() {
        String filePath = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.PALETTE_CUSTOM_PATH, Constants.FileConstants.PALETTE_DEFAULT_PATH);

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
                TileInfo tileInfo = new TileInfo(textureResource, collision);

                RainLogger.RAIN_LOGGER.info("Loaded TileInfo: {} ({})", tileInfo.getTextureName(), textureMapChar);

                tileMappings.put(textureMapChar, tileInfo);
            }

        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Failed to load palette.json", e);
        }
    }

    public TileInfo getTileInfo(char textureMapChar) {
        return tileMappings.getOrDefault(
                textureMapChar,
                new TileInfo(new Resource("rainengine:missing"), false)
        );
    }

    public TileInfo getTileInfo(String textureName) {
        for (TileInfo tileInfo : tileMappings.values()) {
            if (tileInfo.getTextureName().equals(textureName)) {
                return tileInfo;
            }
        }
        return new TileInfo(new Resource("rainengine:missing"),false);
    }

    public TileInfo getTileInfo(Resource textureResource) {
        for (TileInfo tileInfo : tileMappings.values()) {
            if (tileInfo.getTextureResource().equals(textureResource)) {
                return tileInfo;
            }
        }
        return new TileInfo(new Resource("rainengine:missing"), false);
    }


    public boolean hasCollision(char textureMapChar) {
        TileInfo tileInfo = tileMappings.get(textureMapChar);
        return tileInfo != null && tileInfo.isCollision();
    }

    public Iterable<Character> getCollisionTiles() {
        List<Character> collisionTiles = new ArrayList<>();
        for (Map.Entry<Character, TileInfo> entry : tileMappings.entrySet()) {
            if (entry.getValue().isCollision()) {
                collisionTiles.add(entry.getKey());
            }
        }
        return collisionTiles;
    }
}
