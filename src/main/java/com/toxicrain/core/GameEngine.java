package com.toxicrain.core;

//import com.toxicrain.core.json.MapInfoParser;

import com.toxicrain.artifacts.Player;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.PackInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.sound.SoundBuffer;
import com.toxicrain.sound.SoundManager;
import com.toxicrain.sound.SoundSource;
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
import java.util.Random;

import static com.toxicrain.util.TextureUtils.*;
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
    private static SoundManager soundManager;
    private static SoundSource soundSource;
    private static SoundBuffer soundBuffer;


    public GameEngine(){
        fpsUtils = new FPSUtils();
        imguiApp = new ImguiHandler(window);

        SoundManager soundManager = new SoundManager();
        soundManager.init();
        SoundSource soundSource = new SoundSource();
        SoundBuffer soundBuffer = new SoundBuffer("C:\\Users\\hudso\\Downloads/sample-3s.wav");
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
                // Ensure that indices are valid
                if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                    batchRenderer.addTexture(floorTexture, MapInfoParser.mapDataX.get(k), MapInfoParser.mapDataY.get(k), 1, 0, Color.toFloatArray(Color.WHITE)); // Top-right corner
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

        // Starts a new JVM if the application was started on macOS without the -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }

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

        // Create and initialize ImguiHandler
        imguiApp = new ImguiHandler(window);
        imguiApp.initialize();

        Logger.printLOG("Loading pack.json"); //MUST be called before TextureUtils.initTextures()
        PackInfoParser.loadPackInfo();

        Logger.printLOG("Creating Textures");
        TextureUtils.initTextures();

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Set up the projection matrix with FOV of 90 degrees
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(createPerspectiveProjectionMatrix(90.0f, SettingsInfoParser.windowWidth / SettingsInfoParser.windowHeight, 1.0f, 100.0f));
    }

    private static void drawMap(BatchRenderer batchRenderer){
        for (int k = MapInfoParser.mapDataX.size() - 1; k >= 0; k--) {
            // Ensure that indices are valid
            if (k >= 0 && k < MapInfoParser.mapDataY.size() && k >= 0 && k < MapInfoParser.mapDataX.size()) {
                batchRenderer.addTexture(getTexture(MapInfoParser.mapDataType.get(k)), MapInfoParser.mapDataX.get(k), MapInfoParser.mapDataY.get(k), 1, 0, Color.toFloatArray(Color.WHITE)); // Top-right corner
            } else {
                Logger.printLOG("Index out of bounds: space=" + k);
            }
        }

    }
    private static final Random rand = new Random();
    private static TextureInfo getTexture(char textureMapChar) {
        switch (textureMapChar) {
            case ':':
                return floorTexture;
            case '+':
                return concreteTexture1;
            case '-':
                return concreteTexture2;
            case '1':
                return dirtTexture1;
            case '2':
                return dirtTexture2;
            case '3':
                return grassTexture1;
            default:
                return missingTexture;
        }
    }

    private static void loop(BatchRenderer batchRenderer) {
        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            for(int engineFrames = 3; engineFrames >= 0; engineFrames --) { //Put things that need to run 3 times a frame, ex: input
                // Process input
                Player.processInput( window);

            }

            //DO NOT UNCOMMENT WILL NUKE PC
            //soundSource.play(soundBuffer.getBufferId());

            // Check if the window has focus
            boolean windowFocused = glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0;

            // Set the viewport size
            glViewport(0, 0, (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Set up the view matrix
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glTranslatef(-Player.cameraX, -Player.cameraY, -Player.cameraZ);

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            imguiApp.handleInput(window);
            imguiApp.newFrame();
            imguiApp.drawSettingsUI();
            imguiApp.render();

            Vector3f center = getCenter();


            // Begin the batch
            batchRenderer.beginBatch();

            drawMap(batchRenderer);


            // Add textures to the batch
            /*batchRenderer.addTexture(floorTexture, 1, 1, 1, 0, Color.toFloatArray(Color.WHITE)); // Top-left corner

            batchRenderer.addTexture(concreteTexture1, 1, 3, 1, 0, Color.toFloatArray(Color.WHITE)); // Bottom-left corner
            batchRenderer.addTexture(concreteTexture2, 1, 6, 1, 0, Color.toFloatArray(Color.WHITE)); // Bottom-left corner
            batchRenderer.addTexture(missingTexture, 4, 3, 1, 0, Color.toFloatArray(Color.WHITE)); // Bottom-right corner

             */

           // batchRenderer.addTexture(splatterTexture, 2, 1, 1.01f, 0, Color.toFloatArray(0.4f, Color.WHITE));

            float[] openglMousePos = new float[2];
            if (windowFocused) {
                // Get mouse position relative to window
                MouseUtils mouseInput = new MouseUtils(window);
                float[] mousePos = mouseInput.getMousePosition();

                // Convert mouse coordinates to OpenGL coordinates
                openglMousePos = MouseUtils.convertToOpenGLCoordinates(mousePos[0], mousePos[1], (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

            }
            // This is the player!
            batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], Color.toFloatArray(1.0f, Color.WHITE));
            // Render the batch
            batchRenderer.renderBatch();

            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        soundSource.cleanup();
        soundBuffer.cleanup();
        soundManager.cleanup();
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
    private static void enableBlending() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
}
