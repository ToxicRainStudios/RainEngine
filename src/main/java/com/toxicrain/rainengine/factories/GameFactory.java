package com.toxicrain.rainengine.factories;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.toxicrain.rainengine.artifacts.*;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.gui.editor.ImMapEditorMenu;
import com.toxicrain.rainengine.core.LangHelper;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.lua.LuaManager;
import com.toxicrain.rainengine.core.lua.LuaEngine;
import com.toxicrain.rainengine.gui.GuiLuaWrapper;
import com.toxicrain.rainengine.gui.GuiReg;
import com.toxicrain.rainengine.sound.SoundInfo;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.sound.music.MusicManager;
import com.toxicrain.rainengine.util.FileUtils;
import com.toxicrain.rainengine.util.InputUtils;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.toxicrain.rainengine.core.GameEngine.windowManager;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static GuiReg guiReg;
    public static ImMapEditorMenu mapEditorMenu;

    public static Player player;
    public static GuiManager guiManager;
    public static InputUtils inputUtils;
    public static LuaEngine luaEngine;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;
    public static LangHelper langHelper;

    public static void load() {

        // Load player using the atlas region
        player = new Player(new Resource("playerTexture"), false);

        inputUtils = new InputUtils(windowManager);
    }

    public static void loadSounds() {
        SoundSystem.getInstance().init();

        SoundSystem.getInstance().initSounds();

        // Add sounds at runtime
        MusicManager.getInstance().addOrUpdateSound("CALM0", SoundSystem.getSound("Intro"));
        MusicManager.getInstance().addOrUpdateSound("CALM1", SoundSystem.getSound("A1"));
        MusicManager.getInstance().addOrUpdateSound("CALM2", SoundSystem.getSound("A2"));
        MusicManager.getInstance().addOrUpdateSound("CALM3", SoundSystem.getSound("A3"));
        MusicManager.getInstance().addOrUpdateSound("BREAKDOWN", SoundSystem.getSound("Breakdown"));
        MusicManager.getInstance().addOrUpdateSound("COMBAT", SoundSystem.getSound("B1"));
        MusicManager.getInstance().addOrUpdateSound("PANIC1", SoundSystem.getSound("Panic1"));
        MusicManager.getInstance().addOrUpdateSound("PANIC2", SoundSystem.getSound("Panic2"));
        MusicManager.getInstance().addOrUpdateSound("PANIC3", SoundSystem.getSound("Panic3"));


    }

    public static void loadImgui() {
        imguiApp = new ImguiHandler(windowManager);
        imguiApp.initialize("#version 130");
    }

    public static void loadFonts() {
        // GuiBuilder.setFont("dos", FileUtils.getCurrentWorkingDirectory("resources/fonts/Perfect DOS VGA 437.ttf"), 30);
    }

    public static void loadShaders() {
        // fogShaderProgram = ShaderUtils.createShaderProgram(FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_vertex.glsl"), FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_fragment.glsl"));
    }

    public static void loadLua() {
        luaEngine = new LuaEngine();
        guiLuaWrapper = new GuiLuaWrapper();
        functionManager = new LuaManager(luaEngine.getGlobals());
    }

    public static void loadLang() {
        String langTag = SettingsInfoParser.getInstance().getLanguage();
        RainLogger.RAIN_LOGGER.info("Using Lang: {}", langTag);

        String languageTag = langTag.replace('_', '-');
        langHelper = new LangHelper("raiengine", Path.of(FileUtils.getCurrentWorkingDirectory("resources/lang")), Locale.forLanguageTag(languageTag));

        RainLogger.RAIN_LOGGER.info(langHelper.get("greeting"));
    }

    public static void setupGUIs() {
        ImGui.getIO().setConfigFlags(ImGui.getIO().getConfigFlags() | ImGuiConfigFlags.DockingEnable);


        guiReg = new GuiReg();
        mapEditorMenu = new ImMapEditorMenu();
        guiManager.registerGUI("MainMenu", (v) -> guiReg.drawMainMenu());
        guiManager.registerGUI("Settings", (v) -> guiReg.drawSettingsMenu());
        guiManager.registerGUI("Keybinds", (v) -> guiReg.drawKeyBindingInfo());
        guiManager.registerGUI("Inventory", (v) -> guiReg.drawInventory());
        guiManager.registerGUI("FileEditor", (v) -> guiReg.drawFileEditorUI());
        guiManager.registerGUI("Console", (v) -> guiReg.drawConsole());
        guiManager.registerGUI("Debug", (v) -> guiReg.drawDebugInfo());
        guiManager.registerGUI("DeathScreen", (v) -> guiReg.drawDeathScreen());
        guiManager.registerGUI("MapEditor", (v) -> mapEditorMenu.draw());
        //guiManager.addActiveGUI("MapEditor");
        //guiManager.addActiveGUI("Debug");
        //guiManager.addActiveGUI("Keybinds");
        guiManager.addActiveGUI("MainMenu");
    }
}
