package com.toxicrain.rainengine.gui;

import com.github.strubium.windowmanager.imgui.GuiBuilder;
import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.logging.RainConsoleAppender;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.util.FileUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
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

public class GuiReg {

    private int textureID = -1;
    private BufferedImage bufferedImage;

    private String currentDirectory = FileUtils.getUserDir(); // Start in the current directory
    private List<String> filesInDirectory;
    private String selectedFile = null;
    private final ImString fileContent = new ImString(1024 * 18); // 18KB initial buffer size
    private Clip audioClip;

    public GuiReg(){
        loadFilesInDirectory(currentDirectory);

    }

    int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar |
            ImGuiWindowFlags.NoBackground;

    public void runAll(Runnable... tasks) {
        for (Runnable task : tasks) {
            if (task != null) {
                task.run();
            }
        }
    }


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
        float vSyncTotalWidth = vSyncTextWidth + ImGui.getItemRectSize().x + 10;
        ImGui.setCursorPos((screenWidth - vSyncTotalWidth) / 2, screenHeight / 2 - 65);
        ImGui.sameLine();
        if (ImGui.checkbox("##VSyncCheckbox", vSync)) {
            settings.modifySetting("vSync", vSync.get());
        }

        // Window Width Input
        String widthText = GameFactory.langHelper.get("gui.menu.window.width");
        ImFloat windowWidth = new ImFloat(settings.getWindowWidth());
        gui.addTextCentered(widthText, screenHeight / 2 - 40)
                .addFloatInput("##WindowWidth", windowWidth, maxControlWidth);
        settings.modifySetting("windowWidth", windowWidth.get());

        // Window Height Input
        String heightText = GameFactory.langHelper.get("gui.menu.window.height");
        ImFloat windowHeight = new ImFloat(settings.getWindowHeight());
        gui.addTextCentered(heightText, screenHeight / 2)
                .addFloatInput("##WindowHeight", windowHeight, maxControlWidth);
        settings.modifySetting("windowHeight", windowHeight.get());

        // Language Selection ComboBox
        gui.addTextCentered(GameFactory.langHelper.get("gui.menu.lang"), screenHeight / 2 - 120);
        String[] languages = {"English", "Español", "Français"};

        // Determine current language index
        Locale currentLocale = Locale.forLanguageTag(settings.getLanguage());
        int currentLangIndex = 0;
        switch (currentLocale.getLanguage()) {
            case "es":
                currentLangIndex = 1;
                break;
            case "fr":
                currentLangIndex = 2;
                break;
            default:
                currentLangIndex = 0;
                break;
        }

        ImInt selectedLang = new ImInt(currentLangIndex);

        float langComboBoxWidth = maxControlWidth;
        ImGui.setCursorPos((screenWidth - langComboBoxWidth) / 2, screenHeight / 2 - 105);
        ImGui.pushItemWidth(maxControlWidth);

        if (ImGui.combo("##LanguageCombo", selectedLang, languages, languages.length)) {
            Locale newLocale;
            switch (selectedLang.get()) {
                case 1:
                    newLocale = new Locale("es", "ES");
                    break;
                case 2:
                    newLocale = new Locale("fr", "FR");
                    break;
                default:
                    newLocale = new Locale("en", "US");
                    break;
            }

            GameFactory.langHelper.changeLocale(newLocale);
            settings.modifySetting("language", newLocale); // Save language in settings
        }

        ImGui.popItemWidth();

        // FOV Slider
        String fovText = GameFactory.langHelper.get("gui.menu.fov");
        ImFloat fov = new ImFloat(settings.getFOV());
        gui.addTextCentered(fovText, screenHeight / 2 + 40)
                .addSlider("##FovSlider", fov, 30.0f, 120.0f, "%.1f", maxControlWidth);
        settings.modifySetting("fov", fov.get());

        gui.addButtonCentered(GameFactory.langHelper.get("gui.menu.back"), new Runnable() {
            @Override
            public void run() {
                GameFactory.guiManager.removeActiveGUI("Settings");
                GameFactory.guiManager.addActiveGUI("MainMenu");
            }
        }, screenHeight / 2 + 100, 20, 5);

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
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.play"),  () -> runAll(() ->GameFactory.guiManager.removeActiveGUI("MainMenu"), () -> GameFactory.soundSystem.play(SoundSystem.getSound("removeMeClick"), () -> GameEngine.gamePaused = false)), ImGui.getIO().getDisplaySizeY() / 2, 20, 5)


                // Add the centered "Settings" button
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.settings"), () -> {
                    GameFactory.guiManager.removeActiveGUI("MainMenu");
                    GameFactory.guiManager.addActiveGUI("Settings");
                }, ImGui.getIO().getDisplaySizeY() / 2 + 40, 20, 5)

                // Add the centered "Exit" button
                .addButtonCentered(GameFactory.langHelper.get("gui.mainmenu.exit"), () -> System.exit(0), ImGui.getIO().getDisplaySizeY() / 2 + 80, 20, 5)
                .popFont()

                // Add version info at the bottom-right corner
                .addTextAtPosition("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion,
                        ImGui.getIO().getDisplaySizeX() - ImGui.calcTextSize("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion).x - 10,
                        ImGui.getIO().getDisplaySizeY() - ImGui.calcTextSize("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion).y - 10)

                // End window context
                .endWindow();
    }
    public void drawDeathScreen() {

        // Initialize the builder and use it for fluent window building
        GuiBuilder builder = new GuiBuilder();

        // Begin window with flags (transparent, immovable, etc.)
        builder.beginWindow("Death Screen", windowFlags)

                // Add the centered welcome text
                .addTextCentered(GameFactory.langHelper.get("gui.deathscreen.death"), 50)

                // Add the centered "Play" button
                .pushFont("dos")
                .addButtonCentered(GameFactory.langHelper.get("gui.deathscreen.exit"),  () -> runAll(() ->GameFactory.guiManager.removeActiveGUI("DeathScreen"), () ->GameFactory.soundSystem.play(SoundSystem.getSound("removeMeClick"))), ImGui.getIO().getDisplaySizeY() / 2, 20, 5)



                // Add the centered "Exit" button
                .addButtonCentered(GameFactory.langHelper.get("gui.deathscreen.quit"), () -> System.exit(0), ImGui.getIO().getDisplaySizeY() / 2 + 80, 20, 5)
                .popFont()

                // Add version info at the bottom-right corner
                .addTextAtPosition("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion,
                        ImGui.getIO().getDisplaySizeX() - ImGui.calcTextSize("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion).x - 10,
                        ImGui.getIO().getDisplaySizeY() - ImGui.calcTextSize("© 2024 " + GameInfoParser.getInstance().gameMakers + " - " + GameInfoParser.getInstance().gameVersion).y - 10)

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
        for (Map.Entry<String, String> entry : KeyInfoParser.getInstance().getKeyBindings().entrySet()) {
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

    public void drawConsole() {
        GuiBuilder builder = new GuiBuilder();

        builder.beginWindow("Engine Console", windowFlags);

        for (RainConsoleAppender.LogEntry log : RainConsoleAppender.getLogLines()) {
            switch (log.level.levelStr) {
                case "ERROR":
                    ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 0.3f, 0.3f, 1.0f); break;
                case "WARN":
                    ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 0.8f, 0.3f, 1.0f); break;
                case "INFO":
                    ImGui.pushStyleColor(ImGuiCol.Text, 0.6f, 0.9f, 1.0f, 1.0f); break;
                case "DEBUG":
                    ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 1.0f, 0.5f, 1.0f); break;
                default:
                    ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
            }

            ImGui.textWrapped(log.message);
            ImGui.popStyleColor();
        }

        // Auto-scroll if near bottom
        if (ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY(1.0f);
        }

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

                .addText("Player XYZ" + ": " + GameFactory.player.getPosition().toString())

                .addText(GameFactory.langHelper.get("gui.debug.text.music_track") + ": " + GameFactory.musicManager.getCurrentTrackName())


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
