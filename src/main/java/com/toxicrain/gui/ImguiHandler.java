package com.toxicrain.gui;

import com.toxicrain.core.json.SettingsInfoParser;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImFloat;
import org.lwjgl.glfw.GLFW;

/**
 * Handler class for integrating ImGui with GLFW and OpenGL.
 * Provides initialization, input handling, and rendering functions for ImGui.
 */
public class ImguiHandler {

    private ImGuiImplGlfw imguiGlfw;
    private ImGuiImplGl3 imguiGl3;
    private long window;

    /**
     * Constructor for ImguiHandler.
     *
     * @param window the GLFW window handle.
     */
    public ImguiHandler(long window) {
        this.window = window;
    }

    /**
     * Initializes ImGui and sets up OpenGL bindings.
     */
    public void initialize() {
        ImGui.createContext();
        imguiGlfw = new ImGuiImplGlfw();
        imguiGlfw.init(window, true);
        imguiGl3 = new ImGuiImplGl3();
        imguiGl3.init("#version 130"); // OpenGL version
    }

    /**
     * Starts a new ImGui frame.
     */
    public void newFrame() {
        ImGui.getIO().setDisplaySize(SettingsInfoParser.windowWidth, SettingsInfoParser.windowHeight);
        ImGui.newFrame();
    }

    /**
     * Handles keyboard and mouse input.
     *
     * @param window the GLFW window handle.
     */
    public void handleInput(long window) {
        // Handle keyboard input
        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
            int state = GLFW.glfwGetKey(window, key);
            ImGui.getIO().setKeysDown(key, GLFW.GLFW_PRESS == state);
        }

        // Handle mouse input
        ImGui.getIO().setMouseDown(0, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
        ImGui.getIO().setMouseDown(1, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS);
        ImGui.getIO().setMouseDown(2, GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS);

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        GLFW.glfwGetCursorPos(window, mouseX, mouseY);
        ImGui.getIO().setMousePos((float) mouseX[0], (float) mouseY[0]);

        // Handle scroll input
        // ImGui.getIO().setMouseWheel((float) GLFW.glfwGetScrollY(window));
    }

    /**
     * Renders the ImGui frame.
     */
    public void render() {
        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());
    }

    /**
     * Cleans up ImGui resources.
     */
    public static void cleanup() {
        // imguiGl3.dispose();
        // imguiGlfw.dispose();
        ImGui.destroyContext();
    }

    /**
     * Draws the settings UI using ImGui.
     * Provides a window for changing settings in the application.
     */
    public void drawSettingsUI() {
        ImGui.begin("RainEngine Settings");
        ImGui.text("Here is where you can change settings");

        ImGui.setWindowSize(300, 300); // Width and Height in pixels

        ImFloat FOV = new ImFloat(SettingsInfoParser.fov);

        ImGui.beginDisabled(); // Disables all following widgets
        ImGui.checkbox("vSync", SettingsInfoParser.vSync);
        ImGui.sliderFloat("FOV", FOV.getData(), 0, 100);

        ImGui.button("Save");

        ImGui.endDisabled(); // Re-enables widgets

        // End the ImGui window
        ImGui.end();
    }
}
