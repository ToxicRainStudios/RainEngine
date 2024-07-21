package com.toxicrain.core;

//import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.PackInfoParser;
import com.toxicrain.core.render.BatchRenderer;
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

    public static float cameraX = 0.0f; // Camera X position
    public static float cameraY = 0.0f; // Camera Y position
    public static float cameraZ = 5.0f; // Camera Z position
    private static float cameraSpeed = 0.02f; // Camera Speed
    private static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    private static float scrollOffset = 0.0f; // Track the scroll input

    private static boolean fullscreen = true;

    public static void run(String windowTitle) {
        Logger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        Logger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        Logger.printLOG("Running: " + GameInfoParser.gameName + " by " + GameInfoParser.gameMakers);
        Logger.printLOG("Version: " + GameInfoParser.gameVersion);
        doVersionCheck();
        init(windowTitle, true); //TODO vSync should be controllable with some sort of settings menu
        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();

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
        glfwSetWindowSize(window, (int) Constants.windowWidth, (int) Constants.windowHeight);

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
                scrollOffset = (float) yoffset;
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

        Logger.printLOG("Loading pack.json"); //MUST be called before TextureUtils.initTextures()
        PackInfoParser.loadPackInfo(FileUtils.getCurrentWorkingDirectory("resources/json/pack.json"));

        Logger.printLOG("Creating Textures");
        TextureUtils.initTextures();

       /* try {
           MapInfoParser.parseMapFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        enableBlending();
    }
    */
        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }


    private static void loop(BatchRenderer batchRenderer) {


        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            for(int eFrames = 3; eFrames >= 0; eFrames --) { //Put Everything GameEngine-e here. eFrames = engineFrames
                // Process input
                processInput();
            }

            // Check if the window has focus
            boolean windowFocused = glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0;

            // Set the viewport size
            glViewport(0, 0, (int) Constants.windowWidth, (int) Constants.windowHeight);

            // Clear the color and depth buffers
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Set up the projection matrix with FOV of 90 degrees
            glMatrixMode(GL_PROJECTION);
            glLoadMatrixf(createPerspectiveProjectionMatrix(90.0f, Constants.windowWidth / Constants.windowHeight, 1.0f, 100.0f));

            // Set up the view matrix
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glTranslatef(-cameraX, -cameraY, -cameraZ);

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            Vector3f center = getCenter();


            // Begin the batch
            batchRenderer.beginBatch();

            // Add textures to the batch
            batchRenderer.addTexture(floorTexture, 1, 1, 1, 0, Color.toFloatArray(Color.WHITE)); // Top-left corner
            batchRenderer.addTexture(floorTexture, 4, 1, 1, 0, Color.toFloatArray(Color.WHITE)); // Top-right corner
            batchRenderer.addTexture(concreteTexture1, 1, 3, 1, 0, Color.toFloatArray(Color.WHITE)); // Bottom-left corner
            batchRenderer.addTexture(missingTexture, 4, 3, 1, 0, Color.toFloatArray(Color.WHITE)); // Bottom-right corner

            //batchRenderer.addTexture(splatterTexture, 2, 1, 1.01f, 0, Color.toFloatArray(0.4f, Color.WHITE));

            float[] openglMousePos = new float[2];
            if (windowFocused) {
                // Get mouse position relative to window
                MouseUtils mouseInput = new MouseUtils(window);
                float[] mousePos = mouseInput.getMousePosition();

                // Convert mouse coordinates to OpenGL coordinates
                openglMousePos = MouseUtils.convertToOpenGLCoordinates(mousePos[0], mousePos[1], (int) Constants.windowWidth, (int) Constants.windowHeight);

            }
            // This is the player!
            batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], Color.toFloatArray(1.0f, Color.WHITE));
            // Render the batch
            batchRenderer.renderBatch();



            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static void processInput() {
        //Sprinting
        cameraSpeed = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS ? 0.1f : cameraSpeed;

        // Handle left and right movement
        if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
            cameraY += cameraSpeed/2;
            cameraX -= cameraSpeed/2;
        }
        else if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            cameraY -= cameraSpeed/2;
            cameraX -= cameraSpeed/2;
        }
        else if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) cameraX -= cameraSpeed;
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
            cameraY += cameraSpeed/2;
            cameraX += cameraSpeed/2;
        }
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            cameraY -= cameraSpeed/2;
            cameraX += cameraSpeed/2;
        }
        else if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) cameraX += cameraSpeed;
        else if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) cameraY += cameraSpeed;
        else if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) cameraY -= cameraSpeed;


        // Update cameraZ based on the scroll input
        cameraZ += scrollOffset * scrollSpeed;

        // Cap cameraZ at max 25 and min 3
        if (cameraZ > 25) { //TODO Make these configurable
            cameraZ = 25;
        }
        if (cameraZ < 3) {
            cameraZ = 3;
        }

        scrollOffset = 0.0f; // Reset the scroll offset after applying it
    }


    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.engineVersion)) {
            Logger.printLOG("Engine Version check: Pass");
        } else {
            Logger.printERROR("Engine Version check: FAIL");
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
            glfwSetWindowMonitor(window, NULL, (vidmode.width() - (int) Constants.windowWidth) / 2,
                    (vidmode.height() - (int) Constants.windowHeight) / 2, (int) Constants.windowWidth,
                    (int) Constants.windowHeight, GLFW_DONT_CARE);
        }
    }

    private static Vector3f getCenter() {
        FloatBuffer projMatrixBuffer = getPerspectiveProjectionMatrixBuffer();
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.set(projMatrixBuffer);

        // Set up the view matrix
        Matrix4f viewMatrix = new Matrix4f().identity().translate(-cameraX, -cameraY, -cameraZ);

        // Calculate the combined projection and view matrix
        Matrix4f projectionViewMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
        Matrix4f invProjectionViewMatrix = new Matrix4f(projectionViewMatrix).invert();

        // Get the center of the screen in window coordinates
        float screenX = Constants.windowWidth / 2.0f;
        float screenY = Constants.windowHeight / 2.0f;

        // Convert window coordinates to NDC (Normalized Device Coordinates)
        float ndcX = (2.0f * screenX) / Constants.windowWidth - 1.0f;
        float ndcY = 1.0f - (2.0f * screenY) / Constants.windowHeight;

        // Convert NDC to world coordinates
        Vector4f ndcPos = new Vector4f(ndcX, ndcY, -1.0f, 1.0f).mul(invProjectionViewMatrix);
        Vector3f worldPos = new Vector3f(ndcPos.x, ndcPos.y, ndcPos.z).div(ndcPos.w);

        return worldPos;
    }

    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
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
