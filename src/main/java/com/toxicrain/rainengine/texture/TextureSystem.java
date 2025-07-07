package com.toxicrain.rainengine.texture;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.util.FileUtils;

public class TextureSystem {

    private static TextureAtlas textureAtlas;

    public static void initTextures() {
        String textureDirectory = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.IMAGES_PATH);
        textureAtlas = new TextureAtlas(2048);
        textureAtlas.buildAtlas(textureDirectory);
        RainLogger.RAIN_LOGGER.info("Texture atlas built.");
    }

    public static TextureRegion getRegion(Resource location) {
        TextureRegion region = textureAtlas.getRegion(location);
        if (region == null) {
            RainLogger.RAIN_LOGGER.error("Texture region not found: {}", location);
            return textureAtlas.getRegion(new Resource("rainengine:missing")); // fallback
        }
        return region;
    }

    public static int getAtlasTextureId() {
        return textureAtlas.getAtlasTextureId();
    }
}

