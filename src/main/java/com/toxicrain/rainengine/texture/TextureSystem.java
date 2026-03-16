package com.toxicrain.rainengine.texture;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.util.FileUtils;
import lombok.Getter;

@Getter
public class TextureSystem extends BaseInstanceable<TextureSystem> {

    /// The texture atlas we create, {@link TextureSystem} provides wrappers to access this
    private TextureAtlas textureAtlas;

    public static TextureSystem getInstance() {
        return BaseInstanceable.getInstance(TextureSystem.class);
    }

    public void initTextures() {
        String textureDirectory = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.IMAGES_PATH);
        textureAtlas = new TextureAtlas(2048);
        textureAtlas.buildAtlas(textureDirectory);
        RainLogger.RAIN_LOGGER.info("Texture atlas built.");
    }

    public TextureRegion getRegion(Resource location) {
        TextureRegion region = textureAtlas.getRegion(location);
        if (region == null) {
            RainLogger.RAIN_LOGGER.error("Texture region not found: {}", location);
            return textureAtlas.getRegion(new Resource("rainengine:missing")); // fallback
        }
        return region;
    }

    public int getAtlasTextureId() {
        return textureAtlas.getAtlasTextureId();
    }
}

