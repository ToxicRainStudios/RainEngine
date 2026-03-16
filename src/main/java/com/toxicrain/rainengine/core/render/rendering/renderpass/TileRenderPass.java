package com.toxicrain.rainengine.core.render.rendering.renderpass;

import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.PaletteInfoParser;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.core.render.rendering.RenderPass;
import com.toxicrain.rainengine.texture.TextureRegion;
import org.joml.Vector3f;

import java.util.List;

public class TileRenderPass implements RenderPass {

    @Override
    public void render(BatchRenderer batchRenderer, Camera camera) {
        List<float[]> lights = com.toxicrain.rainengine.light.LightSystem.getLightSources();

        int size = MapInfoParser.getInstance().mapData.size();
        for (int k = size - 1; k >= 0; k--) {
            Vector3f pos = MapInfoParser.getInstance().mapData.get(k);
            char textureChar = com.toxicrain.rainengine.core.registries.tiles.Tile.mapDataType.get(k);
            TextureRegion region = PaletteInfoParser.getInstance().getTileInfo(textureChar).getTextureRegion();

            batchRenderer.addTexture(region, pos.x, pos.y, pos.z,
                    new TileParameters(0f, 0f, 0f, 1, 1, null, lights));
        }
    }
}