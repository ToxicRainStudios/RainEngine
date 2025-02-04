package com.toxicrain.gui;

import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.json.KeyInfoParser;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.FileUtils;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
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
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Handler class for integrating ImGui with GLFW and OpenGL.
 * Provides initialization, input handling, and rendering for ImGui.
 *
 * @author strubium
 */
public class ImguiHandler {
    private ImGuiImplGl3 imguiGl3;
    ImFloat FOV = new ImFloat(SettingsInfoParser.getInstance().getFOV());
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
        ImGui.getIO().setDisplaySize(SettingsInfoParser.getInstance().getWindowWidth(), SettingsInfoParser.getInstance().getWindowHeight());
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

    int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar |
            ImGuiWindowFlags.NoBackground;


    /**
     * Draws the settings UI using ImGui.
     */
    public void drawSettingsMenu() {
        // Get the instance of SettingsInfo
        SettingsInfoParser settings = SettingsInfoParser.getInstance();

        // Initialize the GuiBuilder
        GuiBuilder gui = new GuiBuilder();
        
        // Get the display size for full-screen coverage
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float screenHeight = ImGui.getIO().getDisplaySizeY();

        // Start the window with no visuals (full-screen size and position)
        gui.beginWindow("Settings Window", windowFlags);

        // Title centered
        gui.addTextCentered(GameFactory.langHelper.get("gui.menu.settings"), 50);

        // Maximum width for sliders/input fields
        float maxControlWidth = 200.0f;

        // VSync Toggle (Checkbox)
        String vSyncText = GameFactory.langHelper.get("gui.menu.vsync");
        ImBoolean vSync = new ImBoolean(settings.getVsync());
        gui.addTextCentered(vSyncText, screenHeight / 2 - 65);

        // Set up checkbox to be centered horizontally
        float vSyncTextWidth = ImGui.calcTextSize(vSyncText).x;
        float vSyncTotalWidth = vSyncTextWidth + ImGui.getItemRectSize().x + 10; // Text width + checkbox width + spacing
        ImGui.setCursorPos((screenWidth - vSyncTotalWidth) / 2, screenHeight / 2 - 65);
        ImGui.sameLine();
        if (ImGui.checkbox("##VSyncCheckbox", vSync)) {
            settings.modifySetting("vSync", vSync.get());
        }

        // Window Width Input
        String widthText = "Window Width:";
        ImFloat windowWidth = new ImFloat(settings.getWindowWidth());
        gui.addTextCentered(widthText, screenHeight / 2 - 40)
                .addFloatInput("##WindowWidth", windowWidth, maxControlWidth);
        settings.modifySetting("windowWidth", windowWidth.get());

        // Window Height Input
        String heightText = "Window Height:";
        ImFloat windowHeight = new ImFloat(settings.getWindowHeight());
        gui.addTextCentered(heightText, screenHeight / 2)
                .addFloatInput("##WindowHeight", windowHeight, maxControlWidth);
        settings.modifySetting("windowHeight", windowHeight.get());

        // Language Selection ComboBox
        ImInt selectedLang = new ImInt(0); // Default to English
        String[] languages = {"English", "Español", "Français"};
        gui.addTextCentered(GameFactory.langHelper.get("gui.menu.lang"), screenHeight / 2 - 120);

        // Center the combo box
        float langComboBoxWidth = maxControlWidth; // Max width for the combo box
        ImGui.setCursorPos((screenWidth - langComboBoxWidth) / 2, screenHeight / 2 - 105); // Position combo box horizontally centered
        ImGui.pushItemWidth(maxControlWidth);  // Set max width for the combo box

        if (ImGui.combo("##LanguageCombo", selectedLang, languages, languages.length)) {
            String selectedLanguage = languages[selectedLang.get()];
            Locale newLocale;
            if (selectedLanguage.equals("Español")) {
                newLocale = new Locale("es", "ES");
            } else if (selectedLanguage.equals("Français")) {
                newLocale = new Locale("fr", "FR");
            } else {
                newLocale = new Locale("en", "US"); // Default to English
            }
            GameFactory.langHelper.changeLocale(newLocale);
        }

        ImGui.popItemWidth();  // Reset item width after


        // FOV Slider
        String fovText = "Field of View:";
        ImFloat fov = new ImFloat(settings.getFOV());
        gui.addTextCentered(fovText, screenHeight / 2 + 40)
                .addSlider("##FovSlider", fov, 30.0f, 120.0f, "%.1f", maxControlWidth);
        settings.modifySetting("fov", fov.get());

        // Back Button centered
        gui.addButtonCentered("Back", () -> {
            GameFactory.guiManager.removeActiveGUI("Settings");
            GameFactory.guiManager.addActiveGUI("MainMenu");
        }, screenHeight / 2 + 100, 20, 5);

        // Render the GUI
        gui.endWindow();
    }


    public void drawMainMenu() {

        // Initialize the builder and use it for fluent window building
        GuiBuilder builder = new GuiBuilder();

        // Begin window with flags (transparent, immovable, etc.)
        builder.beginWindow("Main Menu Window", windowFlags)

                // Add the centered welcome text
                .addTextCentered(GameFactory.langHelper.get("gui.mainmenu.welcome"), 50)

                // Add the centered "Play" button
                .pushFont("dos")
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.play"), () -> {
                    GameFactory.guiManager.removeActiveGUI("MainMenu");
                }, ImGui.getIO().getDisplaySizeY() / 2, 20, 5)

                // Add the centered "Settings" button
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.settings"), () -> {
                    GameFactory.guiManager.removeActiveGUI("MainMenu");
                    GameFactory.guiManager.addActiveGUI("Settings");
                }, ImGui.getIO().getDisplaySizeY() / 2 + 40, 20, 5)

                // Add the centered "Exit" button
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.exit"), () -> {
                    System.exit(0);
                }, ImGui.getIO().getDisplaySizeY() / 2 + 80, 20, 5)
                .popFont()

                // Add version info at the bottom-right corner
                .addTextAtPosition("© 2024 " + GameInfoParser.gameMakers + " - " + GameInfoParser.gameVersion,
                        ImGui.getIO().getDisplaySizeX() - ImGui.calcTextSize("© 2024 " + GameInfoParser.gameMakers + " - " + GameInfoParser.gameVersion).x - 10,
                        ImGui.getIO().getDisplaySizeY() - ImGui.calcTextSize("© 2024 " + GameInfoParser.gameMakers + " - " + GameInfoParser.gameVersion).y - 10)

                // End window context
                .endWindow();
    }

    public void drawInventory(){
        GuiBuilder builder = new GuiBuilder();

        String string;

        if (GameFactory.player.getEquippedWeapon() == null){
             string = "No Weapon";
        }
        else {
            string = GameFactory.player.getEquippedWeapon().getName();
        }

        builder.beginWindow("Inventory", windowFlags)
            .pushFont("dos")
            .addTextCentered(string, 1)

            .popFont()
            .endWindow();
    }

    public void drawKeyBindingInfo() {
        // Initialize the builder
        GuiBuilder builder = new GuiBuilder();

        // Begin Key Bindings Window
        builder.beginWindow("Key Bindings", windowFlags)

                // Title
                .addTextCentered("Current Key Bindings:", 70.0f);

        // Initial vertical offset for spacing
        float yOffset = 90.0f;

        // Loop through key bindings and display them centered
        for (Map.Entry<String, String> entry : KeyInfoParser.getKeyBindings().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String displayText = GameFactory.langHelper.get("gui.keybinds." + key) + ": " + value;

            // Add centered text with incremental vertical spacing
            builder.addTextCentered(displayText, yOffset);
            yOffset += 20.0f; // Adjust spacing for next entry
        }

        // End Window
        builder.endWindow();
    }




    public void drawDebugInfo() {
        // Initialize the builder
        GuiBuilder builder = new GuiBuilder();

        // Begin Debug Window
        builder.beginWindow("Debug Info", windowFlags)
                .addText(GameFactory.langHelper.get("gui.debug.tile"))

                // FPS Counter
                .addText(GameFactory.langHelper.get("gui.debug.text.fps") + ": " + ImGui.getIO().getFramerate())

                // Memory Usage
                .addText(String.format(GameFactory.langHelper.get("gui.debug.text.mem")+ ": %d MB / %d MB",
                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024),
                        Runtime.getRuntime().totalMemory() / (1024 * 1024)))

                // GLFW Window Info
                .addText(String.format(GameFactory.langHelper.get("gui.debug.text.window") + ": %.0fx%.0f",
                        SettingsInfoParser.getInstance().getWindowWidth(),
                        SettingsInfoParser.getInstance().getWindowHeight()))

                // OpenGL Version
                .addText(GameFactory.langHelper.get("gui.debug.text.opengl") + ": " + GL11.glGetString(GL11.GL_VERSION))

                // End Window
                .endWindow();
    }



    /**
     * Draws the file editor UI using ImGui.
     */
    public void drawFileEditorUI() {
        ImGui.begin("File Editor");

        // Get the available space in the File Editor window
        float windowWidth = ImGui.getContentRegionAvailX();
        float windowHeight = ImGui.getContentRegionAvailY();

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
