package com.toxicrain.rainengine.factories;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.toxicrain.rainengine.artifacts.*;
import com.toxicrain.rainengine.core.registries.manager.NPCManager;
import com.toxicrain.rainengine.core.registries.manager.ProjectileManager;
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
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.InputUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.toxicrain.rainengine.core.GameEngine.windowManager;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static GuiReg guiReg;
    public static SoundSystem soundSystem;
    public static MusicManager musicManager;

    public static Player player;
    public static GuiManager guiManager;
    public static ProjectileManager projectileManager;
    public static InputUtils inputUtils;
    public static LuaEngine luaEngine;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;
    public static LangHelper langHelper;

    public static NPCManager npcManager;

    public static void load(){
        player = new Player(TextureSystem.getTexture("playerTexture"), false);

        inputUtils = new InputUtils(windowManager);
    }

    public static void loadSounds(){
        soundSystem = new SoundSystem();
        soundSystem.init();

        SoundSystem.initSounds();

        //All possible music clips
        Map<String, SoundInfo> sounds = new HashMap<>();
        sounds.put("CALM0", SoundSystem.getSound("Intro"));
        sounds.put("CALM1", SoundSystem.getSound("A1"));
        sounds.put("CALM2", SoundSystem.getSound("A2"));
        sounds.put("CALM3", SoundSystem.getSound("A3"));
        sounds.put("BREAKDOWN", SoundSystem.getSound("Breakdown"));
        sounds.put("COMBAT", SoundSystem.getSound("B1"));
        sounds.put("PANIC1", SoundSystem.getSound("Panic1"));
        sounds.put("PANIC2", SoundSystem.getSound("Panic2"));
        sounds.put("PANIC3", SoundSystem.getSound("Panic3"));


        musicManager = new MusicManager(sounds, soundSystem);

    }

    public static void loadImgui(){
        imguiApp = new ImguiHandler(windowManager);
        imguiApp.initialize("#version 130");
    }

    public static void loadFonts(){
        //GuiBuilder.setFont("dos", FileUtils.getCurrentWorkingDirectory("resources/fonts/Perfect DOS VGA 437.ttf"), 30);
    }

    public static void loadShaders(){
        //fogShaderProgram = ShaderUtils.createShaderProgram(FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_vertex.glsl"), FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_fragment.glsl"));
    }

    public static void loadLua(){
        luaEngine = new LuaEngine();
        guiLuaWrapper = new GuiLuaWrapper();
        functionManager = new LuaManager(luaEngine.getGlobals());

    }

    public static void loadLang(){
        String langTag = SettingsInfoParser.getInstance().getLanguage();
        RainLogger.RAIN_LOGGER.info("Using Lang: {}", langTag);

        //We need to replace "_" with a "-" so it loads correctly
        String languageTag = langTag.replace('_', '-');

        langHelper = new LangHelper("raiengine", Locale.forLanguageTag(languageTag));
        RainLogger.RAIN_LOGGER.info(langHelper.get("greeting"));
    }

    public static void setupGUIs() {
        guiReg = new GuiReg();
        guiManager.registerGUI("MainMenu", (v) -> guiReg.drawMainMenu());
        guiManager.registerGUI("Settings", (v) -> guiReg.drawSettingsMenu());
        guiManager.registerGUI("Keybinds", (v) -> guiReg.drawKeyBindingInfo());
        guiManager.registerGUI("Inventory", (v) -> guiReg.drawInventory());
        guiManager.registerGUI("FileEditor", (v) -> guiReg.drawFileEditorUI());
        guiManager.registerGUI("Console", (v) -> guiReg.drawConsole());
        guiManager.registerGUI("Debug", (v) -> guiReg.drawDebugInfo());
        guiManager.registerGUI("DeathScreen", (v) -> guiReg.drawDeathScreen());
        guiManager.addActiveGUI("Inventory");
        guiManager.addActiveGUI("Debug");
        guiManager.addActiveGUI("Keybinds");
        guiManager.addActiveGUI("MainMenu");
    }
}