package com.toxicrain.core;

import com.toxicrain.core.json.*;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.artifacts.Tile;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.light.LightSystem;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


@UtilityClass
public class GameEngine {

    // The window handle
    public static WindowManager windowManager;

    public static final boolean menu = false;

    public static void run() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashReporter());
        RainLogger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        RainLogger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        RainLogger.printLOG("Running: " + GameInfoParser.gameName + " by " + GameInfoParser.gameMakers);
        RainLogger.printLOG("Version: " + GameInfoParser.gameVersion);
        doVersionCheck();

        RainLogger.printLOG("Loading Lua");
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


    private static void init() {
        // Set up an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        RainLogger.printLOG("Initializing GLFW");
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        RainLogger.printLOG("Creating Game Window");
        windowManager.createWindow(GameInfoParser.defaultWindowName, SettingsInfoParser.getInstance().getVsync());

        RainLogger.printLOG("Loading IMGUI");
        // Create and initialize ImguiHandler
        GameFactory.imguiApp = new ImguiHandler(windowManager.getWindow());
        GameFactory.imguiApp.initialize();

        RainLogger.printLOG("Creating Textures");
        TextureSystem.initTextures();

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        RainLogger.printLOG("Creating OpenGL Capabilities");
        GL.createCapabilities();

        RainLogger.printLOG("Loading Keybinds");
        KeyInfoParser.loadKeyInfo();

        // Set the "background" color
        glClearColor(0, 0, 0, 0);

        // Set up the projection matrix with FOV of 90 degrees
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(createPerspectiveProjectionMatrix(SettingsInfoParser.getInstance().getFOV(), SettingsInfoParser.getInstance().getWindowWidth() / SettingsInfoParser.getInstance().getWindowHeight(), 1.0f, 100.0f));

        GameFactory.load();

        RainLogger.printLOG("Loading Fonts");
        GameFactory.loadFonts();

        RainLogger.printLOG("Loading Map Palette");
        PaletteInfoParser.loadTextureMappings();

        RainLogger.printLOG("Loading ImGUI");
        GameFactory.loadImgui();

        // Set the viewport size
        glViewport(0, 0, (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight());

        RainLogger.printLOG("Initializing SoundSystem");
        GameFactory.soundSystem.init();
        SoundSystem.initSounds();

        // Weapons must be loaded after sounds have been loaded
        GameFactory.loadWeapons();

        RainLogger.printLOG("Loading Shaders");
        GameFactory.loadShaders();

        GameFactory.player.addWeapon(GameFactory.shotgun);

        windowManager.doOpenGLSetup();

        LuaManager.executePostInitScripts();

        GameFactory.setupGUIs();

        GameFactory.loadNPC();

        RainLogger.printLOG("Loading Lang");
        GameFactory.loadLang();
    }

    private static void drawMap(BatchRenderer batchRenderer) {
        // Ensure the texture mappings have been loaded
        if (PaletteInfoParser.textureMappings == null) {
            throw new IllegalStateException("Texture mappings not loaded! Call PaletteInfoParser.loadTextureMappings() first.");
        }

        for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
            // Ensure that indices are valid
            if (k >= 0 && k < MapInfoParser.mapDataY.size() && k < MapInfoParser.mapDataX.size()) {
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
                        LightSystem.getLightSources()
                ); // Top-right corner
            } else {
                RainLogger.printLOG("Index out of bounds: space=" + k);
            }
        }
    }

    private static long lastFrameTime = System.nanoTime();

    private static void update(float deltaTime) {
        for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {
            GameFactory.player.update(deltaTime);
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


        batchRenderer.setBlendingEnabled(true);
        // Render the batch
        batchRenderer.renderBatch();

        batchRenderer.setBlendingEnabled(false);

        GameFactory.imguiApp.handleInput(windowManager.getWindow());
        GameFactory.imguiApp.newFrame();
        GameFactory.guiManager.render(); // Outputs: Rendering Main Menu
        LuaManager.executeAllImguiScripts();
        GameFactory.imguiApp.render();
        // Swap buffers and poll events
        windowManager.swapAndPoll();
    }

    private static void loop(BatchRenderer batchRenderer) {
        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!windowManager.shouldClose()) {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f; // Convert nanoseconds to seconds
            lastFrameTime = currentTime;


            update(deltaTime);
            render(batchRenderer);
        }
        ImguiHandler.cleanup();
        GameFactory.soundSystem.cleanup();
    }

    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.engineVersion)) {
            RainLogger.printLOG("Engine Version check: Pass");
        } else {
            RainLogger.printERROR("Engine Version check: FAIL");
            RainLogger.printERROR("Certain features may not work as intended");
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
