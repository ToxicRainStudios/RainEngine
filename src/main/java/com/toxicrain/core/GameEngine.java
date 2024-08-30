package com.toxicrain.core;

//import com.toxicrain.core.json.MapInfoParser;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.artifacts.Projectile;
import com.toxicrain.core.json.*;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.render.Tile;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.gui.Menu;
import com.toxicrain.util.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.toxicrain.util.TextureUtils.floorTexture;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;




public class GameEngine {

    // The window handle
    public static long window;

    private static boolean fullscreen = true;

    private static final boolean menu = false;

    public GameEngine(){


    }

    public static void run(String windowTitle) {
        Logger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        Logger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        Logger.printLOG("Running: " + GameInfoParser.gameName + " by " + GameInfoParser.gameMakers);
        Logger.printLOG("Version: " + GameInfoParser.gameVersion);
        doVersionCheck();
        Logger.printLOG("Loading User Settings");
        SettingsInfoParser.loadSettingsInfo();

        Logger.printLOG("Loading Map Data");
        try {
            MapInfoParser.parseMapFile("map");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        init(windowTitle, SettingsInfoParser.vSync);
        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();
        batchRenderer.setBlendingEnabled(true);


            for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
                // Ensure that indices are valid
                if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                    batchRenderer.addTexture(floorTexture, MapInfoParser.mapDataX.get(k), MapInfoParser.mapDataY.get(k), 1, 0, 1,1, Color.toFloatArray(Color.WHITE)); // Top-right corner
                } else {
                    Logger.printLOG("Index out of bounds: space=" + k);
                }
            }

        loop(batchRenderer);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    private static void init(String windowTitle, boolean vSync) {
        // Set up an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        Logger.printLOG("Initializing GLFW");
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, 0); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, 1); // the window will be resizable

        Logger.printLOG("Creating Game Window");
        // Create the window
        window = glfwCreateWindow(300, 300, windowTitle, glfwGetPrimaryMonitor(), NULL);
        // Resize the window
        glfwSetWindowSize(window, (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // This is detected in the rendering loop
            if (glfwGetKey(window, GLFW_KEY_F11) == GLFW_PRESS) {
                toggleFullscreen();
            }
        });
        // Create and set the scroll callback
        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                Player.scrollOffset = (float) yoffset;
            }
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(vSync ? 1 : 0);
        glfwShowWindow(window);

        GL.createCapabilities();

        Logger.printLOG("Loading IMGUI");
        // Create and initialize ImguiHandler
        GameFactory.imguiApp = new ImguiHandler(window);
        GameFactory.imguiApp.initialize();

        Logger.printLOG("Loading pack.json"); //MUST be called before TextureUtils.initTextures()
        PackInfoParser.loadPackInfo();

        Logger.printLOG("Creating Textures");
        TextureUtils.initTextures();

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
        glLoadMatrixf(createPerspectiveProjectionMatrix(SettingsInfoParser.fov, SettingsInfoParser.windowWidth / SettingsInfoParser.windowHeight, 1.0f, 100.0f));


        GameFactory.load();

        Logger.printLOG("Loading Menu");
        if(menu){
            Menu.initializeMenu();
        }

        Logger.printLOG("Loading Map Palette");
        PaletteInfoParser.loadTextureMappings();

        // Set the viewport size
        glViewport(0, 0, (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);


        Logger.printLOG("Initializing SoundSystem");
        GameFactory.soundSystem.init();
        GameFactory.loadSounds();

        Logger.printLOG("Loading Shaders");
        GameFactory.loadShaders();

        Logger.printLOG("Loading Lua");
        GameFactory.loadlua();
        LuaManager.loadScript("example.lua");


        GameFactory.player.addWeapon(GameFactory.pistol);

    }

    private static void drawMap(BatchRenderer batchRenderer) {
        // Ensure the texture mappings have been loaded
        if (PaletteInfoParser.textureMappings == null) {
            throw new IllegalStateException("Texture mappings not loaded! Call PaletteInfoParser.loadTextureMappings() first.");
        }

        for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
            // Ensure that indices are valid
            if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                char textureChar = Tile.mapDataType.get(k);  // Get the character representing the texture
                TextureInfo textureInfo = PaletteInfoParser.getTexture(textureChar);  // Get the TextureInfo from TextureLoader

                batchRenderer.addTextureLit(
                        textureInfo,
                        MapInfoParser.mapDataX.get(k),
                        MapInfoParser.mapDataY.get(k),
                        1,
                        0,
                        1,
                        1,
                        LightUtils.getLightSources()
                ); // Top-right corner
            } else {
                Logger.printLOG("Index out of bounds: space=" + k);
            }
        }
    }

    private static long lastFrameTime = System.nanoTime();

    private static void update(float deltaTime) {
        for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {
            GameFactory.player.update(deltaTime);
            GameFactory.character.runAI(GameFactory.character);
            GameFactory.projectile.update();
        }

        if (menu) {
            // Check mouse position and button press
            float[] mousePos = GameFactory.mouseUtils.getMousePosition();
            boolean mouseClick = GameFactory.mouseUtils.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
            Player.cameraZ = 25;
            Menu.updateMenu(mousePos[0], mousePos[1], mouseClick);
        }
    }

    private static void render(BatchRenderer batchRenderer) {
        // Clear the color and depth buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set up the view matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(-Player.cameraX, -Player.cameraY, -Player.cameraZ);

        // Begin the batch
        batchRenderer.beginBatch();

        if (glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0) {
            GameFactory.imguiApp.handleInput(window);
            GameFactory.imguiApp.newFrame();
            GameFactory.imguiApp.drawSettingsUI();
            GameFactory.imguiApp.drawFileEditorUI();
            GameFactory.imguiApp.render();
        }

        if (menu) {
            Menu.render(batchRenderer);
        } else {
            drawMap(batchRenderer);
            NPC.render(batchRenderer, GameFactory.character);
            Projectile.render(batchRenderer, GameFactory.projectile);
            Player.render(batchRenderer);
        }

        // Render the batch
        batchRenderer.renderBatch();
        // Swap buffers and poll events
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private static void loop(BatchRenderer batchRenderer) {
        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
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

    /**
     * Toggles fullscreen for the game window
     */
    private static void toggleFullscreen() {
        fullscreen = !fullscreen;

        // Get the primary monitor
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);

        if (fullscreen) {
            // Switch to fullscreen mode
            glfwSetWindowMonitor(window, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
        } else {
            // Switch back to windowed mode
            glfwSetWindowMonitor(window, NULL, (vidmode.width() - (int) SettingsInfoParser.windowWidth) / 2,
                    (vidmode.height() - (int) SettingsInfoParser.windowHeight) / 2, (int) SettingsInfoParser.windowWidth,
                    (int) SettingsInfoParser.windowHeight, GLFW_DONT_CARE);
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
