package com.toxicrain.core;

import com.toxicrain.factories.GameFactory;
import lombok.Getter;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Deals with the creation and destruction of RainEngine's window
 *
 * @author strubium
 */
public class WindowManager {

    @Getter
    private long window;  // Window handle
    private boolean fullscreen;
    private final int windowWidth;
    private final int windowHeight;

    public WindowManager(int width, int height, boolean fullscreen) {
        this.windowWidth = width;
        this.windowHeight = height;
        this.fullscreen = fullscreen;
    }

    public void createWindow(String windowTitle, boolean vSync) {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure window settings
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);  // Window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // Window will be resizable

        long monitor = fullscreen ? glfwGetPrimaryMonitor() : NULL;
        window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, monitor, NULL);

        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        centerWindow();

        glfwMakeContextCurrent(window);
        glfwSwapInterval(vSync ? 1 : 0); // Enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities(); // This line is critical for LWJGL's interoperation with GLFW's OpenGL context

        // Set up key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
            if (key == GLFW_KEY_F11 && action == GLFW_PRESS) {
                toggleFullscreen();
            }
        });
        // Create and set the scroll callback
        glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                GameFactory.player.scrollOffset = (float) yoffset;
            }
        });
    }

    private void centerWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidMode != null) {
                glfwSetWindowPos(
                        window,
                        (vidMode.width() - pWidth.get(0)) / 2,
                        (vidMode.height() - pHeight.get(0)) / 2
                );
            }
        }
    }

    public void toggleFullscreen() {
        fullscreen = !fullscreen;

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (fullscreen) {
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
        } else {
            glfwSetWindowMonitor(window, NULL, (vidMode.width() - windowWidth) / 2, (vidMode.height() - windowHeight) / 2, windowWidth, windowHeight, GLFW_DONT_CARE);
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void swapAndPoll(){
        swapBuffers();
        pollEvents();
    }

    public void doOpenGLSetup(){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK); // Cull back faces
    }

    public boolean isFocused(){
        return glfwGetWindowAttrib(window, GLFW_FOCUSED) != 0;
    }

    public void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

}
