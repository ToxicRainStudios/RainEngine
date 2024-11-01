package com.toxicrain.gui;

import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.FileUtils;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImFloat;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Handler class for integrating ImGui with GLFW and OpenGL.
 * Provides initialization, input handling, and rendering for ImGui.
 *
 * @author strubium
 */
public class ImguiHandler {

    public static boolean imguiWindowOpen = true;
    private ImGuiImplGl3 imguiGl3;
    ImFloat FOV = new ImFloat(SettingsInfoParser.fov);
    private final long window;
    private int textureID = -1;
    private BufferedImage bufferedImage;

    private String currentDirectory = FileUtils.getUserDir(); // Start in the current directory
    private List<String> filesInDirectory;
    private String selectedFile = null;
    private final ImString fileContent = new ImString(1024 * 18); // 18KB initial buffer size
    private Clip audioClip;

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
        ImGui.destroyContext();
    }

    /**
     * Draws the settings UI using ImGui.
     */
    public void drawSettingsUI() {
        ImGui.begin("RainEngine " + GameFactory.langHelper.get("settings"));
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

    public void drawMainMenu() {
        // Set window flags to make the ImGui window transparent and immovable
        int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoBackground;

        // Get the display size for full-screen coverage
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float screenHeight = ImGui.getIO().getDisplaySizeY();

        // Position the ImGui window at the top-left corner
        ImGui.setNextWindowPos(0, 0);
        // Set the window size to cover the entire screen
        ImGui.setNextWindowSize(screenWidth, screenHeight);

        ImGui.begin("Invisible Window", windowFlags); // Begin ImGui "window" with no visuals

        // Center text by adding padding (optional)
        ImGui.setCursorPos((screenWidth - ImGui.calcTextSize(GameFactory.langHelper.get("gui.mainmenu.welcome")).x) / 2, 50);
        ImGui.text(GameFactory.langHelper.get("gui.mainmenu.welcome"));

        // Center buttons by calculating the offset for each button
        ImGui.setCursorPos((screenWidth - 100) / 2, screenHeight / 2);
        if (ImGui.button(GameFactory.langHelper.get("gui.mainmenu.play"), 100, 30)) {
            System.out.println("Start Game button clicked!");
        }

        ImGui.setCursorPos((screenWidth - 100) / 2, (screenHeight / 2) + 40);
        if (ImGui.button(GameFactory.langHelper.get("gui.mainmenu.settings"), 100, 30)) {
            System.out.println("Settings button clicked!");
        }

        ImGui.setCursorPos((screenWidth - 100) / 2, (screenHeight / 2) + 80);
        if (ImGui.button(GameFactory.langHelper.get("gui.mainmenu.exit"), 100, 30)) {
            System.out.println("Exit button clicked!");
        }

        String versionInfo = "© 2024 Toxic Rain - " + GameInfoParser.gameVersion;
        float textWidth = ImGui.calcTextSize(versionInfo).x;
        float textHeight = ImGui.calcTextSize(versionInfo).y;
        ImGui.setCursorPos(screenWidth - textWidth - 10, screenHeight - textHeight - 10); // Adjusts padding from the edges
        ImGui.text(versionInfo); // Display version info

        // End the ImGui window context
        ImGui.end();
    }


    /**
     * Draws the file editor UI using ImGui.
     */
    public void drawFileEditorUI() {
        ImGui.begin("File Editor");

        // Get the available space in the File Editor window
        float windowWidth = ImGui.getContentRegionAvailX();
        float windowHeight = ImGui.getContentRegionAvailY();

        if (ImGui.isWindowFocused()) {
            imguiWindowOpen = true;
        }
        else {
            imguiWindowOpen = false;
        }

        // File Browser
        ImGui.beginChild("File Browser", windowWidth * 0.3f, windowHeight, true); // 30% width for File Browser

        // Button to go to parent directory
        if (!currentDirectory.equals(Paths.get(currentDirectory).getRoot().toString())) {
            if (ImGui.selectable("^")) {
                navigateToParentDirectory();
            }
        }

        for (String fileName : filesInDirectory) {
            Path filePath = Paths.get(currentDirectory, fileName);
            if (Files.isDirectory(filePath)) {
                if (ImGui.selectable("[FOLDER] " + fileName)) {
                    navigateToDirectory(filePath.toString());
                }
            } else {
                if (ImGui.selectable(fileName, fileName.equals(selectedFile))) {
                    selectedFile = fileName;
                    if (fileName.endsWith(".png")) {
                        loadPngFile(filePath.toString());
                    } else if (fileName.endsWith(".wav")) {
                        playWavFile(filePath.toString());
                    } else {
                        loadFileContent(filePath.toString());
                    }
                }
            }
        }
        ImGui.endChild();

        // File Content Editor
        ImGui.sameLine();
        ImGui.beginChild("File Content", windowWidth, windowHeight, true);
        if (selectedFile != null && selectedFile.endsWith(".png") && textureID != -1) {
            ImGui.image(textureID, windowWidth * 0.7f, windowHeight * 0.7f);
            if (ImGui.button("Refresh Image")) {
                clearImage();
            }
        }else if (selectedFile != null && selectedFile.endsWith(".wav")) {
            if (ImGui.button("Stop Audio")) {
                stopWavFile();
            }
        }
        else if (selectedFile != null && !selectedFile.endsWith(".png")) {
            ImGui.inputTextMultiline("##source", fileContent, ImGuiInputTextFlags.AllowTabInput | ImGuiInputTextFlags.AutoSelectAll);
            if (ImGui.button("Save")) {
                saveFileContent(Paths.get(currentDirectory, selectedFile).toString());
            }
        }
        ImGui.endChild();
        ImGui.end();
    }

    /**
     * Loads a .png file and creates an OpenGL texture.
     *
     * @param filePath the path to the .png file
     */
    private void loadPngFile(String filePath) {
        try {
            bufferedImage = ImageIO.read(new File(filePath));
            textureID = createTextureFromImage(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the currently loaded image and deletes the texture.
     */
    private void clearImage() {
        if (textureID != -1) {
            GL11.glDeleteTextures(textureID);
            textureID = -1;
            bufferedImage = null;
        }
    }

    /**
     * Creates an OpenGL texture from a BufferedImage.
     *
     * @param image the BufferedImage to convert
     * @return the OpenGL texture ID
     */
    private int createTextureFromImage(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                buffer.put((byte) (pixel & 0xFF));         // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
            }
        }

        buffer.flip();

        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

    /**
     * Plays the given .wav file.
     *
     * @param filePath the path to the .wav file
     */
    private void playWavFile(String filePath) {
        try {
            stopWavFile();  // Stop any previously playing audio
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the currently playing .wav file.
     */
    private void stopWavFile() {
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
            audioClip = null;
        }
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
