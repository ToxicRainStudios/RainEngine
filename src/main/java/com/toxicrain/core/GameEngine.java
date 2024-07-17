package com.toxicrain.core;

import com.toxicrain.core.json.gameinfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.Constants;
import com.toxicrain.util.TextureUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.toxicrain.util.TextureUtil.floorTexture;
import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameEngine {

    // The window handle
    public static long window;

    private static float cameraX = 0.0f; // Camera X position
    private static float cameraY = 0.0f; // Camera Y position
    private static float cameraZ = 5.0f; // Camera Z position
    private static float cameraSpeed = 0.05f; // Camera Speed

    private static boolean fullscreen = false;

    public static void run(String windowTitle) {
        Logger.printLOG("Hello LWJGL " + Version.getVersion() + "!");
        Logger.printLOG("Hello RainEngine " + Constants.engineVersion + "!");
        Logger.printLOG("Running: " + gameinfoParser.gameName + " by " + gameinfoParser.gameMakers);
        Logger.printLOG("Version: " + gameinfoParser.gameVersion);
        doVersionCheck();
        init(windowTitle, true);
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void init(String windowTitle, boolean vSync) {
        // Setup an error callback. The default implementation will print the error message in System.err.
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
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true); // This is detected in the rendering loop
            if (glfwGetKey(window, GLFW_KEY_F11) == GLFW_PRESS) {
                toggleFullscreen();
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
        Logger.printLOG("Init Textures");
        TextureUtil.initTextures();
    }

    private static void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();

        // Run the rendering loop until the user has attempted to close the window/pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Set the viewport size
            glViewport(0, 0, 1920, 1080);

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

            // Begin the batch
            batchRenderer.beginBatch();

            // Add textures to the batch
            batchRenderer.addTexture(floorTexture,1,1,1);

            batchRenderer.addTexture(floorTexture,2,1,1);

            // Render the batch
            batchRenderer.renderBatch();

            // Process input
            processInput();

            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }


    private static void renderTexture(TextureInfo textureInfo) {
        // Enable textures
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureInfo.textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Calculate aspect ratio
        float aspectRatio = (float) textureInfo.width / textureInfo.height;

        // Render a quad with the texture
        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 0.0f);
        glVertex3f(-aspectRatio, -1.0f, 0.0f);
        glTexCoord2f(1.0f, 0.0f);
        glVertex3f(aspectRatio, -1.0f, 0.0f);
        glTexCoord2f(1.0f, 1.0f);
        glVertex3f(aspectRatio, 1.0f, 0.0f);
        glTexCoord2f(0.0f, 1.0f);
        glVertex3f(-aspectRatio, 1.0f, 0.0f);
        glEnd();

        // Disable textures
        glDisable(GL_TEXTURE_2D);
    }

    private static void processInput() {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) cameraZ -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) cameraZ += cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) cameraX -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) cameraX += cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) cameraY -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS) cameraY += cameraSpeed;
    }

    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(gameinfoParser.engineVersion)) {
            Logger.printLOG("Engine Version check: Pass");
        } else {
            Logger.printERROR("Engine Version check: FAIL");
        }
    }

    /**
     * Toggles fullscreen for the window
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

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        buffer.put(projectionMatrix).flip();
        return buffer;
    }
}
