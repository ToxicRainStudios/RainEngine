package com.toxicrain.gui;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.SettingsInfoParser;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImFloat;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Handler class for integrating ImGui with GLFW and OpenGL.
 * Provides initialization, input handling, and rendering for ImGui.
 *
 * @author strubium
 */
public class ImguiHandler {

    private ImGuiImplGl3 imguiGl3;
    ImFloat FOV = new ImFloat(SettingsInfoParser.fov);
    private final long window;

    private String currentDirectory = System.getProperty("user.dir"); // Start in the current directory
    private List<String> filesInDirectory;
    private String selectedFile = null;
    private ImString fileContent = new ImString(1024 * 18); // 18KB initial buffer size

    /**
     * Constructor for ImguiHandler.
     *
     * @param window the GLFW window handle.
     */
    public ImguiHandler(long window) {
        this.window = window;
        loadFilesInDirectory(currentDirectory);
    }

    /**
     * Initializes ImGui and sets up OpenGL bindings.
     */
    public void initialize() {
        ImGui.createContext();
        ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
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
     * Handles the keyboard and mouse input for IMGUI
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
     */
    public void drawSettingsUI() {
        ImGui.begin("RainEngine Settings");
        ImGui.text("Here is where you can change settings");

        ImGui.setWindowSize(300, 300); // Width and Height in pixels

        ImGui.sliderFloat("FOV", FOV.getData(), 0, 120);

        ImGui.beginDisabled(); // Disables all following widgets
        ImGui.checkbox("vSync", SettingsInfoParser.vSync);

        ImGui.endDisabled(); // Re-enables widgets
        if(ImGui.button("Save")){
            SettingsInfoParser.modifyKey("fov", String.valueOf(FOV));
        }

        // End the ImGui window
        ImGui.end();
    }

    /**
     * Draws the file editor UI using ImGui.
     */
    public void drawFileEditorUI() {
        ImGui.begin("File Editor");

        // File Browser
        ImGui.beginChild("File Browser", 200, 400, true);

        // Button to go to parent directory
        if (!currentDirectory.equals(Paths.get(currentDirectory).getRoot().toString())) {
            if (ImGui.selectable("..")) {
                navigateToParentDirectory();
            }
        }

        for (String fileName : filesInDirectory) {
            Path filePath = Paths.get(currentDirectory, fileName);
            if (Files.isDirectory(filePath)) {
                if (ImGui.selectable("[DIR] " + fileName)) {
                    navigateToDirectory(filePath.toString());
                }
            } else {
                if (ImGui.selectable(fileName, fileName.equals(selectedFile))) {
                    selectedFile = fileName;
                    loadFileContent(filePath.toString());
                }
            }
        }
        ImGui.endChild();

        // File Content Editor
        ImGui.sameLine();
        ImGui.beginChild("File Content", 500, 400, true);
        if (selectedFile != null) {
            ImGui.inputTextMultiline("##source", fileContent, ImGuiInputTextFlags.AllowTabInput | ImGuiInputTextFlags.AutoSelectAll);
            if (ImGui.button("Save")) {
                saveFileContent(Paths.get(currentDirectory, selectedFile).toString());
            }
        }
        ImGui.endChild();

        ImGui.end();
    }

    /**
     * Loads the files in the specified directory.
     *
     * @param directoryPath the path of the directory.
     */
    private void loadFilesInDirectory(String directoryPath) {
        try {
            filesInDirectory = Files.list(Paths.get(directoryPath))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the content of the selected file into the editor.
     *
     * @param filePath the path of the file.
     */
    private void loadFileContent(String filePath) {
        Path path = Path.of(filePath);

        // Check if the path is a directory
        if (Files.isDirectory(path)) {
            System.err.println("Cannot open a directory: " + filePath);
            return;
        }

        try {
            String content = Files.readString(path);
            fileContent.set(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the content of the editor back to the file.
     *
     * @param filePath the path of the file.
     */
    private void saveFileContent(String filePath) {
        try {
            Files.writeString(Path.of(filePath), fileContent.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate to the specified directory.
     *
     * @param directoryPath the path of the directory.
     */
    private void navigateToDirectory(String directoryPath) {
        currentDirectory = directoryPath;
        loadFilesInDirectory(currentDirectory);
        selectedFile = null;
        fileContent.clear();
    }

    /**
     * Navigate to the parent directory.
     */
    private void navigateToParentDirectory() {
        Path parentPath = Paths.get(currentDirectory).getParent();
        if (parentPath != null) {
            navigateToDirectory(parentPath.toString());
        }
    }
}
