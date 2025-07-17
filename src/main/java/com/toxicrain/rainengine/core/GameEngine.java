package com.toxicrain.rainengine.core;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.github.strubium.windowmanager.window.WindowManager;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.eventbus.RainBusListener;
import com.toxicrain.rainengine.core.eventbus.events.*;
import com.toxicrain.rainengine.core.eventbus.events.load.LoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.RenderGuiEvent;
import com.toxicrain.rainengine.core.json.*;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.core.registries.tiles.Tile;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;


@UtilityClass
public class GameEngine {

    // The window handle
    public static WindowManager windowManager;

    public static void run() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashReporter());
        RainLogger.buildLoggers();

        RainLogger.RAIN_LOGGER.info("Hello LWJGL {}!", Version.getVersion());
        RainLogger.RAIN_LOGGER.info("Hello RainEngine " + Constants.engineVersion + "!");
        RainLogger.RAIN_LOGGER.info("Running: {} by {}", GameInfoParser.getInstance().gameName, GameInfoParser.getInstance().gameMakers);
        RainLogger.RAIN_LOGGER.info("Version: {}", GameInfoParser.getInstance().gameVersion);
        doVersionCheck();

        RainLogger.RAIN_LOGGER.info("Loading Event Bus");
        RainBusListener.addEventListeners();

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.PRE));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.ININT));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.MANAGER));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.POST));

        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();

        loop(batchRenderer);

        // Free the window callbacks and destroy the window
        windowManager.destroy();
    }

    public static void drawMap(BatchRenderer batchRenderer) {
        // Ensure the texture mappings have been loaded
        if (PaletteInfoParser.tileMappings == null) {
            throw new IllegalStateException("Texture mappings not loaded! Call PaletteInfoParser.loadTextureMappings() first.");
        }

        int size = MapInfoParser.getInstance().mapData.size();  // Get the size once

        for (int k = size - 1; k >= 0; k--) {
            // Get the TilePos object
            TilePos pos = MapInfoParser.getInstance().mapData.get(k);

            // Get the character representing the texture
            char textureChar = Tile.mapDataType.get(k);

            TextureRegion region = PaletteInfoParser.getInstance().getTileInfo(textureChar).getTextureRegion();

            // Render the tile with lighting
            batchRenderer.addTexture(
                    region,
                    pos.x,
                    pos.y,
                    pos.z,
                    new TileParameters(0f, 0f,0f, 1,1,null, LightSystem.getLightSources())

            );
        }
    }

    private static void render(BatchRenderer batchRenderer) {
        // Clear the color and depth buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set up the view matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(-GameFactory.player.getPosition().x, -GameFactory.player.getPosition().y, -GameFactory.player.getPosition().z);

        // Begin the batch
        batchRenderer.beginBatch();

        SmeagleBus.getInstance().post(new DrawMapEvent(batchRenderer));

        GameFactory.npcManager.render(batchRenderer);
        GameFactory.projectileManager.render(batchRenderer);
        GameFactory.player.render(batchRenderer);

        // Render the batch
        batchRenderer.renderBatch();

        GameFactory.imguiApp.handleInput(windowManager.window);
        GameFactory.imguiApp.newFrame();

        SmeagleBus.getInstance().post(new RenderGuiEvent());

        GameFactory.imguiApp.render();

        // Swap buffers and poll events
        windowManager.swapAndPoll();

    }

    public static boolean gamePaused = true;

    private static void loop(BatchRenderer batchRenderer) {
        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!windowManager.shouldClose()) {
            DeltaTimeUtil.update();

            SmeagleBus.getInstance().post(new GameUpdateEvent(gamePaused));

            render(batchRenderer);
        }
        GameFactory.imguiApp.cleanup();
        GameFactory.soundSystem.cleanup();
    }

    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.getInstance().engineVersion)) {
            RainLogger.RAIN_LOGGER.info("Engine Version check: Pass");
        } else {
            RainLogger.RAIN_LOGGER.error("Engine Version check: FAIL");
            RainLogger.RAIN_LOGGER.error("Certain features may not work as intended");
        }
    }

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    /**
     * Creates a perspective projection matrix.
     *
     * @param fov the field of view angle in degrees
     * @param aspectRatio the aspect ratio of the viewport (width/height)
     * @param near the distance to the near clipping plane
     * @param far the distance to the far clipping plane
     * @return a FloatBuffer containing the perspective projection matrix
     */
    public static FloatBuffer createPerspectiveProjectionMatrix(float fov, float aspectRatio, float near, float far) {
        float f = (float) (1.0f / Math.tan(Math.toRadians(fov) / 2.0));
        float[] projectionMatrix = new float[16];

        projectionMatrix[0] = f / aspectRatio;
        projectionMatrix[1] = 0.0f;
        projectionMatrix[2] = 0.0f;
        projectionMatrix[3] = 0.0f;

        projectionMatrix[4] = 0.0f;
        projectionMatrix[5] = f;
        projectionMatrix[6] = 0.0f;
        projectionMatrix[7] = 0.0f;

        projectionMatrix[8] = 0.0f;
        projectionMatrix[9] = 0.0f;
        projectionMatrix[10] = (far + near) / (near - far);
        projectionMatrix[11] = -1.0f;

        projectionMatrix[12] = 0.0f;
        projectionMatrix[13] = 0.0f;
        projectionMatrix[14] = (2 * far * near) / (near - far);
        projectionMatrix[15] = 0.0f;

        buffer.put(projectionMatrix).flip();
        return buffer;
    }


    /**
     * Gets the perspective projection matrix.
     * @return The FloatBuffer containing the perspective projection matrix
     */
    public static FloatBuffer getPerspectiveProjectionMatrixBuffer() {
        return buffer;
    }

}
