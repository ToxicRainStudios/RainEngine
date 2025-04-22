package com.toxicrain.factories;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.toxicrain.artifacts.*;
import com.toxicrain.artifacts.behavior.BehaviorSequence;
import com.toxicrain.artifacts.behavior.FollowPlayerSeeingBehavior;
import com.toxicrain.artifacts.behavior.LookAtPlayerSeeingBehavior;
import com.toxicrain.artifacts.manager.NPCManager;
import com.toxicrain.artifacts.manager.ProjectileManager;
import com.toxicrain.core.LangHelper;
import com.toxicrain.core.RainLogger;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.lua.LuaEngine;
import com.toxicrain.gui.GuiLuaWrapper;
import com.toxicrain.gui.GuiReg;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.sound.music.MusicManager;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.InputUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.toxicrain.core.GameEngine.windowManager;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static GuiReg guiReg;
    public static SoundSystem soundSystem;
    public static MusicManager musicManager;

    public static Player player;
    public static GuiManager guiManager;
    public static ProjectileManager projectileManager;
    public static Projectile projectile;
    public static InputUtils inputUtils;
    public static Weapon pistol;
    public static Weapon rifle;
    public static Weapon shotgun;
    public static LuaEngine luaEngine;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;
    public static LangHelper langHelper;

    public static NPCManager npcManager;
    public static NPC character;
    public static BossNPC bossNPC;

    public static void load(){
        player = new Player(5, 5, 5, TextureSystem.getTexture("playerTexture"), false);

        projectileManager = new ProjectileManager();
        projectile = new Projectile(MapInfoParser.playerx,MapInfoParser.playery,0.001f,0, TextureSystem.getTexture("playerTexture"));

        inputUtils = new InputUtils(windowManager.window);
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
        guiManager = new GuiManager();
        imguiApp = new ImguiHandler(windowManager);
        imguiApp.initialize("#version 130");
    }

    public static void loadFonts(){
        //GuiBuilder.setFont("dos", FileUtils.getCurrentWorkingDirectory("resources/fonts/Perfect DOS VGA 437.ttf"), 30);
    }

    public static void loadWeapons(){
        pistol = new Weapon("Pistol", 3, 20,1,1, TextureSystem.getTexture("bullet"),120, 0.9f, "Breakdown");
        rifle = new Weapon("Rifle", 3, 20,1,1, TextureSystem.getTexture("bullet"), 60, 0.2f,"Breakdown");
        shotgun = new Weapon("Shotgun", 30, 20,5,4, TextureSystem.getTexture("bullet"), 400, 0.1f,"Breakdown");
    }

    public static void loadNPC(){
        npcManager = new NPCManager();

        character = new NPC(12,-4,1, Size.AVERAGE.getSize());
        bossNPC = new BossNPC(12,-4, 10,  Size.AVERAGE.getSize(), 1000);
        LookAtPlayerSeeingBehavior lookAtPlayerSeeingBehavior = new LookAtPlayerSeeingBehavior();
        bossNPC.setBehaviorSequence(new BehaviorSequence(lookAtPlayerSeeingBehavior));
        character.setBehaviorSequence(new BehaviorSequence(new FollowPlayerSeeingBehavior(00.1f), lookAtPlayerSeeingBehavior));

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
        guiManager.registerGUI("Debug", (v) -> guiReg.drawDebugInfo());

        guiManager.addActiveGUI("Inventory");
        guiManager.addActiveGUI("Debug");
        guiManager.addActiveGUI("Keybinds");
        guiManager.addActiveGUI("MainMenu");
    }
}