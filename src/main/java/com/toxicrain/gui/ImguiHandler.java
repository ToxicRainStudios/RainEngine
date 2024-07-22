package com.toxicrain.gui;

import com.toxicrain.core.json.SettingsInfoParser;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

public class ImguiHandler {

    private ImGuiImplGlfw imguiGlfw;
    private ImGuiImplGl3 imguiGl3;
    private long window;

    public ImguiHandler(long window) {
        this.window = window;
    }

    public void initialize() {
        // Initialize ImGui and set up OpenGL bindings
        ImGui.createContext();
        imguiGlfw = new ImGuiImplGlfw();
        imguiGlfw.init(window, true);
        imguiGl3 = new ImGuiImplGl3();
        imguiGl3.init("#version 130"); // OpenGL version
    }

    public void newFrame() {
        // Start a new ImGui frame
        ImGui.getIO().setDisplaySize(SettingsInfoParser.windowWidth, SettingsInfoParser.windowHeight);
        ImGui.newFrame();
    }

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
        //ImGui.getIO().setMouseWheel((float) GLFW.glfwGetScrollY(window));
    }

    public void render() {
        // Start a new ImGui frame
        ImGui.render();
        imguiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void cleanup() {
        imguiGl3.dispose();
        imguiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void drawUI() {
        // Start a new ImGui window
        ImGui.begin("Test Window");

        ImGui.setWindowSize(300, 200); // Width and Height in pixels

        // Add a button and check if it's pressed
        if (ImGui.button("Click Me!")) {
            System.out.println("Button clicked!");
        }

        // Optionally add more UI elements here
        ImGui.text("This is a sample text");

        // End the ImGui window
        ImGui.end();
    }
}
