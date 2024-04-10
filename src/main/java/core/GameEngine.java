package core;

import map.MapLoader;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import util.constant.Constants;
import util.movement.Camera;

import java.nio.IntBuffer;

import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
public class  GameEngine {

    //The window handle
    public static long window;

    private static float cameraX = 0.0f; //Camera X position
    private static float cameraY = 0.0f; //Camera Y position
    private static float cameraZ = 5.0f; //Camera Z position
    private static float cameraSpeed = 0.05f; //Camera Speed

    public static void run(String windowTile) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init(windowTile, true);
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    private static void init(String windowTitle, boolean vSync) {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Starts a new JVM if the application was started on macOS without the
        // -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }
        // Create the window
        window = glfwCreateWindow(300, 300, windowTitle, glfwGetPrimaryMonitor(), NULL);
        // Resize the window
        glfwSetWindowSize(window, (int) Constants.windowWidth, (int) Constants.windowHeight);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

            // Enable v-sync
            if (vSync){
                glfwSwapInterval(1);
                glfwShowWindow(window);
            }
            else {
                glfwSwapInterval(0);
                glfwShowWindow(window);
            }
    }
    private static void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // Set the viewport size
            glViewport(0, 0, 1920, 1080);

            // Clear the color and depth buffers
            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Set up the projection matrix
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            glFrustum(Camera.left, Camera.right, Camera.bottom, Camera.top, Camera.near, Camera.far);

            // Set up the view matrix
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            // Translate the camera
            glTranslatef(-cameraX, -cameraY, -cameraZ);

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            // Draw the shapes
            MapLoader.LoadMap();

            // Process input
            processInput();

            // Swap buffers and poll events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private static void processInput() {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            cameraZ -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            cameraZ += cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
            cameraX -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
            cameraX += cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS)
            cameraY -= cameraSpeed;
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS)
            cameraY += cameraSpeed;
    }

}