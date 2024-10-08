package com.toxicrain.factories;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.artifacts.Projectile;
import com.toxicrain.artifacts.Weapon;
import com.toxicrain.core.GameEngine;
import com.toxicrain.core.lua.LuaManager;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.lua.LuaEngine;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.gui.GuiLuaWrapper;
import com.toxicrain.sound.SoundInfo;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.MouseUtils;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static SoundSystem soundSystem;

    public static Player player;
    public static Projectile projectile;
    public static NPC character;
    public static MouseUtils mouseUtils;
    public static Weapon pistol;
    public static Weapon rifle;
    public static Weapon shotgun;
    public static SoundInfo sampleSound;
    public static LuaEngine luaEngine;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;


    public static void load(){
        player = new Player(5, 5, 5, TextureSystem.getTexture("playerTexture"), false);
        imguiApp = new ImguiHandler(GameEngine.window);
        imguiApp.initialize();
        soundSystem = new SoundSystem();

        projectile = new Projectile(MapInfoParser.playerx,MapInfoParser.playery,0.001f,0, TextureSystem.getTexture("playerTexture"));
        character = new NPC(12,-4,1);
        mouseUtils = new MouseUtils(GameEngine.window);

        pistol = new Weapon("Pistol", 3, 20,1,1);
        rifle = new Weapon("Rifle", 3, 20,1,1);
        shotgun = new Weapon("Shotgun", 30, 20,4,5);

    }
    public static void loadSounds(){
        sampleSound = soundSystem.loadSound("resources/sound/Sample.wav");
    }

    public static void loadShaders(){
        //fogShaderProgram = ShaderUtils.createShaderProgram(FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_vertex.glsl"), FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_fragment.glsl"));
    }

    public static void loadlua(){
        luaEngine = new LuaEngine();
        guiLuaWrapper = new GuiLuaWrapper();
        functionManager = new LuaManager(luaEngine.getGlobals());

    }
}