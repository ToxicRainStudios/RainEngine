package com.toxicrain.factories;

import com.toxicrain.artifacts.*;
import com.toxicrain.artifacts.behavior.BehaviorSequence;
import com.toxicrain.artifacts.behavior.FollowPlayerSeeingBehavior;
import com.toxicrain.artifacts.behavior.LookAtPlayerSeeingBehavior;
import com.toxicrain.artifacts.manager.NPCManager;
import com.toxicrain.artifacts.manager.ProjectileManager;
import com.toxicrain.core.LangHelper;
import com.toxicrain.core.RainLogger;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.lua.LuaEngine;
import com.toxicrain.gui.GuiManager;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.gui.GuiLuaWrapper;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.MouseUtils;

import java.util.Locale;

import static com.toxicrain.core.GameEngine.windowManager;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static SoundSystem soundSystem;

    public static Player player;
    public static GuiManager guiManager;
    public static ProjectileManager projectileManager;
    public static Projectile projectile;
    public static MouseUtils mouseUtils;
    public static Weapon pistol;
    public static Weapon rifle;
    public static Weapon shotgun;
    public static LuaEngine luaEngine;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;
    public static LangHelper langHelper;

    public static NPCManager npcManager;
    public static NPC character;

    public static void load(){
        player = new Player(5, 5, 5, TextureSystem.getTexture("playerTexture"), false);
        guiManager = new GuiManager();
        imguiApp = new ImguiHandler(windowManager.getWindow());
        imguiApp.initialize();
        soundSystem = new SoundSystem();

        projectileManager = new ProjectileManager();
        projectile = new Projectile(MapInfoParser.playerx,MapInfoParser.playery,0.001f,0, TextureSystem.getTexture("playerTexture"));

        mouseUtils = new MouseUtils(windowManager.getWindow());
    }

    public static void loadWeapons(){
        pistol = new Weapon("Pistol", 3, 20,1,1, TextureSystem.getTexture("bullet"),120, 0.9f, "Sample");
        rifle = new Weapon("Rifle", 3, 20,1,1, TextureSystem.getTexture("bullet"), 60, 0.2f,"Sample");
        shotgun = new Weapon("Shotgun", 30, 20,5,4, TextureSystem.getTexture("bullet"), 35, 0.1f,"Sample");
    }

    public static void loadNPC(){
        npcManager = new NPCManager();

        character = new NPC(12,-4,1, Size.AVERAGE.getSize());
        LookAtPlayerSeeingBehavior lookAtPlayerSeeingBehavior = new LookAtPlayerSeeingBehavior();
        character.setBehaviorSequence(new BehaviorSequence(new FollowPlayerSeeingBehavior(00.1f), lookAtPlayerSeeingBehavior));

    }

    public static void loadShaders(){
        //fogShaderProgram = ShaderUtils.createShaderProgram(FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_vertex.glsl"), FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_fragment.glsl"));
    }

    public static void loadlua(){
        luaEngine = new LuaEngine();
        guiLuaWrapper = new GuiLuaWrapper();
        functionManager = new LuaManager(luaEngine.getGlobals());

    }

    public static void loadLang(){
        langHelper = new LangHelper("raiengine", Locale.ENGLISH);
        RainLogger.printLOG(langHelper.get("greeting"));
    }

    public static void setupGUIs() {
        guiManager.registerGUI("MainMenu", (v) -> imguiApp.drawMainMenu());
        guiManager.registerGUI("Settings", (v) -> imguiApp.drawSettingsMenu());
        guiManager.registerGUI("Inventory", (v) -> imguiApp.drawInventory());
        guiManager.registerGUI("FileEditor", (v) -> imguiApp.drawFileEditorUI());
        guiManager.registerGUI("Debug", (v) -> imguiApp.drawDebugInfo());

        guiManager.addActiveGUI("Inventory");
        guiManager.addActiveGUI("MainMenu");
    }
}