package com.toxicrain.core;

import com.toxicrain.artifacts.behavior.*;
import com.toxicrain.core.json.*;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.artifacts.Tile;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.gui.Menu;
import com.toxicrain.light.LightSystem;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.*;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.toxicrain.core.json.SettingsInfoParser.windowHeight;
import static com.toxicrain.core.json.SettingsInfoParser.windowWidth;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;



@UtilityClass
public class GameEngine {

    // The window handle
    public static WindowManager windowManager;

    private static boolean fullscreen = true;

    public static final boolean menu = false;

    public static void run() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashReporter());
        Logger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        Logger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        Logger.printLOG("Running: " + GameInfoParser.gameName + " by " + GameInfoParser.gameMakers);
        Logger.printLOG("Version: " + GameInfoParser.gameVersion);
        doVersionCheck();
        Logger.printLOG("Loading User Settings");
        SettingsInfoParser.loadSettingsInfo();

        Logger.printLOG("Loading Lua");
        GameFactory.loadlua();
        LuaManager.categorizeScripts("resources/scripts/");
        LuaManager.executeInitScripts();

        windowManager = new WindowManager((int) windowWidth, (int) windowHeight, true);

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

        Logger.printLOG("Initializing GLFW");
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        Logger.printLOG("Creating Game Window");
        windowManager.createWindow(GameInfoParser.defaultWindowName, SettingsInfoParser.vSync);

        Logger.printLOG("Loading IMGUI");
        // Create and initialize ImguiHandler
        GameFactory.imguiApp = new ImguiHandler(windowManager.getWindow());
        GameFactory.imguiApp.initialize();

        Logger.printLOG("Creating Textures");
        TextureSystem.initTextures();

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        Logger.printLOG("Creating OpenGL Capabilities");
        GL.createCapabilities();

        Logger.printLOG("Loading Keybinds");
        KeyInfoParser.loadKeyInfo();

        // Set the "background" color
        glClearColor(0, 0, 0, 0);

        // Set up the projection matrix with FOV of 90 degrees
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(createPerspectiveProjectionMatrix(SettingsInfoParser.fov, windowWidth / windowHeight, 1.0f, 100.0f));


        GameFactory.load();

        Logger.printLOG("Loading Menu");
        if(menu){
            try {
                Menu.initializeMenu();
            } catch (IOException | FontFormatException e) {
                throw new RuntimeException(e);
            }
        }

        Logger.printLOG("Loading Map Palette");
        PaletteInfoParser.loadTextureMappings();

        // Set the viewport size
        glViewport(0, 0, (int) windowWidth, (int) windowHeight);

        Logger.printLOG("Initializing SoundSystem");
        GameFactory.soundSystem.init();
        SoundSystem.initSounds();

        Logger.printLOG("Loading Shaders");
        GameFactory.loadShaders();

        GameFactory.player.addWeapon(GameFactory.pistol);

        windowManager.doOpenGLSetup();

        LuaManager.executePostInitScripts();
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
                Logger.printLOG("Index out of bounds: space=" + k);
            }
        }
    }

    private static long lastFrameTime = System.nanoTime();

    private static void update(float deltaTime) {
        FollowPlayerBehavior followPlayerBehavior = new FollowPlayerBehavior(5.0f);
        LookAtPlayerBehavior lookAtPlayerBehavior = new LookAtPlayerBehavior();
        LookAtPlayerSeeingBehavior lookAtPlayerSeeingBehavior = new LookAtPlayerSeeingBehavior();


        BehaviorSequence behaviorSequence2 = new BehaviorSequence(new FollowPlayerSeeingBehavior(00.1f), lookAtPlayerSeeingBehavior);
        BehaviorSequence behaviorSequence = new BehaviorSequence(followPlayerBehavior, lookAtPlayerBehavior);

        for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {
            GameFactory.player.update(deltaTime);
            behaviorSequence2.execute(GameFactory.character);

            GameFactory.projectile.update();
        }

        LuaManager.executeTickScripts();

        if (menu) {
            GameFactory.player.cameraZ = 25;
            Menu.updateMenu();
        }
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

        if (menu) {
            Menu.render(batchRenderer);
        } else {
            drawMap(batchRenderer);
            GameFactory.character.render(batchRenderer);
            GameFactory.projectile.render(batchRenderer);
            GameFactory.player.render(batchRenderer);
        }


        batchRenderer.setBlendingEnabled(true);
        // Render the batch
        batchRenderer.renderBatch();

        batchRenderer.setBlendingEnabled(false);

        if (windowManager.isFocused()) {
            GameFactory.imguiApp.handleInput(windowManager.getWindow());
            GameFactory.imguiApp.newFrame();
            GameFactory.imguiApp.drawSettingsUI();
            GameFactory.imguiApp.drawFileEditorUI();
            LuaManager.executeAllImguiScripts();
            GameFactory.imguiApp.render();
        }

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
            Logger.printLOG("Engine Version check: Pass");
        } else {
            Logger.printERROR("Engine Version check: FAIL");
            Logger.printERROR("Certain features may not work as intended");
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
