package com.toxicrain.core;

import com.github.strubium.windowmanager.window.WindowManager;
import com.toxicrain.core.json.*;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.registries.WeaponRegistry;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.registries.tiles.Tile;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.light.LightSystem;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
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
        RainLogger.RAIN_LOGGER.info("Running: {} by {}", GameInfoParser.gameName, GameInfoParser.gameMakers);
        RainLogger.RAIN_LOGGER.info("Version: {}", GameInfoParser.gameVersion);
        doVersionCheck();

        RainLogger.RAIN_LOGGER.info("Loading Lua");
        GameFactory.loadLua();
        LuaManager.categorizeScripts("resources/scripts/");
        LuaManager.executeInitScripts();
        Tile.combineTouchingAABBs();

        windowManager = new WindowManager((int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), true);

        init();
        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();

        loop(batchRenderer);

        // Free the window callbacks and destroy the window
        windowManager.destroy();
    }

    /**
     * Does all the loading for RainEngine.
     */
    private static void init() {

        RainLogger.RAIN_LOGGER.info("Creating Game Window");
        windowManager.createWindow(GameInfoParser.defaultWindowName, SettingsInfoParser.getInstance().getVsync());
        windowManager.setupDefaultKeys();

        // Create and set the scroll callback
        glfwSetScrollCallback(windowManager.window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                GameFactory.player.scrollOffset = (float) yoffset;
            }
        });

        RainLogger.RAIN_LOGGER.info("Creating Textures");
        TextureSystem.initTextures();

        RainLogger.RAIN_LOGGER.info("Loading Keybinds");
        KeyInfoParser.loadKeyInfo();

        // Set the "background" color
        glClearColor(0, 0, 0, 0);

        // Set up the projection matrix with FOV of 90 degrees
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(createPerspectiveProjectionMatrix(SettingsInfoParser.getInstance().getFOV(), SettingsInfoParser.getInstance().getWindowWidth() / SettingsInfoParser.getInstance().getWindowHeight(), 1.0f, 100.0f));

        GameFactory.load();

        RainLogger.RAIN_LOGGER.info("Loading ImGUI");
        GameFactory.loadImgui();

        RainLogger.RAIN_LOGGER.info("Loading Fonts");
        GameFactory.loadFonts();

        RainLogger.RAIN_LOGGER.info("Loading Map Palette");
        PaletteInfoParser.loadTextureMappings();

        // Set the viewport size
        glViewport(0, 0, (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight());

        RainLogger.RAIN_LOGGER.info("Initializing SoundSystem");
        GameFactory.loadSounds();

        // Weapons must be loaded after sounds have been loaded
        GameFactory.loadWeapons();

        RainLogger.RAIN_LOGGER.info("Loading Shaders");
        GameFactory.loadShaders();

        GameFactory.player.addWeapon(WeaponRegistry.get("Shotgun"));

        LuaManager.executePostInitScripts();

        GameFactory.setupGUIs();

        GameFactory.loadNPC();

        RainLogger.RAIN_LOGGER.info("Loading Lang");
        GameFactory.loadLang();


        //"COMBAT" is the normal track, "PANIC" is the low health track, "CALM" is the quiet track
        GameFactory.musicManager.setStartingSound("CALM0");
        GameFactory.musicManager.start();
        GameFactory.musicManager.setNextTrack("CALM1");
    }

    private static void drawMap(BatchRenderer batchRenderer) {
        // Ensure the texture mappings have been loaded
        if (PaletteInfoParser.textureMappings == null) {
            throw new IllegalStateException("Texture mappings not loaded! Call PaletteInfoParser.loadTextureMappings() first.");
        }

        int sizeX = MapInfoParser.mapDataX.size();  // Get the size once
        int sizeY = MapInfoParser.mapDataY.size();
        int sizeZ = MapInfoParser.mapDataZ.size();

        for (int k = sizeX - 1; k >= 0; k--) {
            if (k < sizeY && k < sizeZ) {  // Only check bounds once
                char textureChar = Tile.mapDataType.get(k);  // Get the character representing the texture
                TextureInfo textureInfo = PaletteInfoParser.getTexture(textureChar);  // Get the TextureInfo from TextureLoader

                batchRenderer.addTextureLit(
                        textureInfo,
                        MapInfoParser.mapDataX.get(k),
                        MapInfoParser.mapDataY.get(k),
                        MapInfoParser.mapDataZ.get(k).floatValue(),
                        0,
                        1,
                        1,
                        LightSystem.getLIGHT_SOURCES()
                );
            } else {
                RainLogger.RAIN_LOGGER.info("Index out of bounds: space={}", k);
            }
        }
    }

    private static long lastFrameTime = System.nanoTime();

    private static void update(float deltaTime) {
        GameFactory.player.update(deltaTime);
        for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {
            GameFactory.npcManager.update(deltaTime);

            GameFactory.projectileManager.update(deltaTime);
        }
        LuaManager.executeTickScripts();
    }

    private static void render(BatchRenderer batchRenderer) {
        // Clear the color and depth buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set up the view matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(-GameFactory.player.cameraX, -GameFactory.player.cameraY, -GameFactory.player.cameraZ);

        // Begin the batch
        batchRenderer.beginBatch();

        drawMap(batchRenderer);
        GameFactory.npcManager.render(batchRenderer);
        GameFactory.projectileManager.render(batchRenderer);
        GameFactory.player.render(batchRenderer);


        // Render the batch
        batchRenderer.renderBatch();

        GameFactory.imguiApp.handleInput(windowManager.window);
        GameFactory.imguiApp.newFrame();
        GameFactory.guiManager.render();
        LuaManager.executeAllImguiScripts();
        GameFactory.imguiApp.render();
        // Swap buffers and poll events
        windowManager.swapAndPoll();

    }

    private static void loop(BatchRenderer batchRenderer) {
        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!windowManager.shouldClose()) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1000000000.0f; // Convert nanoseconds to seconds
            lastFrameTime = currentTime;


            update(deltaTime);
            render(batchRenderer);
        }
        GameFactory.imguiApp.cleanup();
        GameFactory.soundSystem.cleanup();
    }

    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.engineVersion)) {
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
    private static FloatBuffer createPerspectiveProjectionMatrix(float fov, float aspectRatio, float near, float far) {
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
