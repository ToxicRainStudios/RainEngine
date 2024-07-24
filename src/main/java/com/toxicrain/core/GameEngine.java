package com.toxicrain.core;

//import com.toxicrain.core.json.MapInfoParser;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.PackInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.render.Tile;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
import static com.toxicrain.util.TextureUtils.playerTexture;
import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;




public class GameEngine {

    // The window handle
    public static long window;

    private static boolean fullscreen = true;
    private static FPSUtils fpsUtils;
    private static ImguiHandler imguiApp;
    private static Vector3f center;
    private static SoundSystem soundSystem = new SoundSystem();
    private static int bufferId;

    private static NPC npc;
    private static Player player;

    private static long lastTime;
    private static double deltaTime;

    public GameEngine() {
        fpsUtils = new FPSUtils();
        imguiApp = new ImguiHandler(window);
    }

    public static void run(String windowTitle) {
        Logger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        Logger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        Logger.printLOG("Running: " + GameInfoParser.gameName + " by " + GameInfoParser.gameMakers);
        Logger.printLOG("Version: " + GameInfoParser.gameVersion);
        doVersionCheck();
        Logger.printLOG("Loading User Settings");
        SettingsInfoParser.loadSettingsInfo();

        init(windowTitle, SettingsInfoParser.vSync);
        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();
        MapInfoParser mapInfoParser = new MapInfoParser();
        try {
            mapInfoParser.parseMapFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
            if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                batchRenderer.addTexture(floorTexture, MapInfoParser.mapDataX.get(k), MapInfoParser.mapDataY.get(k), 1, 0, Color.toFloatArray(Color.WHITE)); // Top-right corner
            } else {
                Logger.printLOG("Index out of bounds: space=" + k);
            }
        }

        lastTime = System.nanoTime();

        loop(batchRenderer);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void init(String windowTitle, boolean vSync) {
        GLFWErrorCallback.createPrint(System.err).set();

        Logger.printLOG("Initializing GLFW");
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, 0);
        glfwWindowHint(GLFW_RESIZABLE, 1);

        if (startNewJvmIfRequired()) {
            System.exit(0);
        }

        Logger.printLOG("Creating Game Window");
        window = glfwCreateWindow(300, 300, windowTitle, glfwGetPrimaryMonitor(), NULL);
        glfwSetWindowSize(window, (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
            if (glfwGetKey(window, GLFW_KEY_F11) == GLFW_PRESS) {
                toggleFullscreen();
            }
        });

        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            Player.scrollOffset = (float) yoffset;
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(vSync ? 1 : 0);
        glfwShowWindow(window);

        GL.createCapabilities();

        imguiApp = new ImguiHandler(window);
        imguiApp.initialize();

        Logger.printLOG("Loading pack.json");
        PackInfoParser.loadPackInfo();

        Logger.printLOG("Creating Textures");
        TextureUtils.initTextures();

        Logger.printLOG("Creating OpenGL Capabilities");
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(createPerspectiveProjectionMatrix(SettingsInfoParser.fov, SettingsInfoParser.windowWidth / SettingsInfoParser.windowHeight, 1.0f, 100.0f));

        player = new Player(Player.cameraX, Player.cameraY, Player.cameraZ, playerTexture, false);
        npc = new NPC(100, 100, 50, playerTexture);

        glViewport(0, 0, (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);
        glEnable(GL_DEPTH_TEST);

        Logger.printLOG("Initializing SoundSystem");
        soundSystem.init();
        bufferId = soundSystem.loadSound("C:/Users/hudso/Downloads/sample-3s.wav");
    }

    private static void drawMap(BatchRenderer batchRenderer) {
        for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
            if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                batchRenderer.addTexture(TextureUtils.getTexture(Tile.mapDataType.get(k)), MapInfoParser.mapDataX.get(k), MapInfoParser.mapDataY.get(k), 1, 0, Color.toFloatArray(Color.WHITE));
            } else {
                Logger.printLOG("Index out of bounds: space=" + k);
            }
        }
    }

    private static void update() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
        lastTime = currentTime;

        for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {
            Player.processInput(window);
        }
        center = getCenter();
        npc.update();
    }

    private static void render(BatchRenderer batchRenderer) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(-Player.cameraX, -Player.cameraY, -Player.cameraZ);

        batchRenderer.beginBatch();

        drawMap(batchRenderer);

        float[] openglMousePos = new float[2];
        if (glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0) {
            imguiApp.handleInput(window);
            imguiApp.newFrame();
            imguiApp.drawSettingsUI();
            imguiApp.render();

            MouseUtils mouseInput = new MouseUtils(window);
            float[] mousePos = mouseInput.getMousePosition();

            soundSystem.play(bufferId);

            openglMousePos = MouseUtils.convertToOpenGLCoordinates(mousePos[0], mousePos[1], (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);
        }

        batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], Color.toFloatArray(1.0f, Color.WHITE));

        npc.render(batchRenderer);

        batchRenderer.renderBatch();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private static void loop(BatchRenderer batchRenderer) {
        while (!glfwWindowShouldClose(window)) {
            update();
            render(batchRenderer);
        }
        ImguiHandler.cleanup();
        soundSystem.cleanup();
    }



    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.engineVersion)) {
            Logger.printLOG("Engine Version check: Pass");
        } else {
            Logger.printERROR("Engine Version check: FAIL");
            Logger.printERROR("Certain features may not work as intended");
        }
    }

    private static void toggleFullscreen() {
        fullscreen = !fullscreen;

        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);

        if (fullscreen) {
            glfwSetWindowMonitor(window, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
        } else {
            glfwSetWindowMonitor(window, NULL, (vidmode.width() - (int) SettingsInfoParser.windowWidth) / 2,
                    (vidmode.height() - (int) SettingsInfoParser.windowHeight) / 2, (int) SettingsInfoParser.windowWidth,
                    (int) SettingsInfoParser.windowHeight, GLFW_DONT_CARE);
        }
    }
    private static Vector3f getCenter() {
        FloatBuffer projMatrixBuffer = getPerspectiveProjectionMatrixBuffer();
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.set(projMatrixBuffer);

        // Set up the view matrix
        Matrix4f viewMatrix = new Matrix4f().identity().translate(-Player.cameraX, -Player.cameraY, -Player.cameraZ);

        // Calculate the combined projection and view matrix
        Matrix4f projectionViewMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
        Matrix4f invProjectionViewMatrix = new Matrix4f(projectionViewMatrix).invert();

        // Get the center of the screen in window coordinates
        float screenX = SettingsInfoParser.windowWidth / 2.0f;
        float screenY = SettingsInfoParser.windowHeight / 2.0f;

        // Convert window coordinates to NDC (Normalized Device Coordinates)
        float ndcX = (2.0f * screenX) / SettingsInfoParser.windowWidth - 1.0f;
        float ndcY = 1.0f - (2.0f * screenY) / SettingsInfoParser.windowHeight;

        // Convert NDC to world coordinates
        Vector4f ndcPos = new Vector4f(ndcX, ndcY, -1.0f, 1.0f).mul(invProjectionViewMatrix);

        return new Vector3f(ndcPos.x, ndcPos.y, ndcPos.z).div(ndcPos.w);
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

    public static FloatBuffer getPerspectiveProjectionMatrixBuffer() {
        return buffer;
    }
}
