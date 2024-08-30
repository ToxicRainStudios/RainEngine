package com.toxicrain.factories;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.artifacts.Projectile;
import com.toxicrain.artifacts.Weapon;
import com.toxicrain.core.GameEngine;
import com.toxicrain.core.LuaManager;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.lua.LuaEngine;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.util.FileUtils;
import com.toxicrain.util.MouseUtils;
import com.toxicrain.util.ShaderUtils;
import org.luaj.vm2.*;

import static com.toxicrain.util.TextureUtils.playerTexture;

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
    public static int sampleSound;
    public static int fogShaderProgram;
    public static LuaEngine luaEngine;
    public static LuaManager functionManager;


    public static void load(){
        imguiApp = new ImguiHandler(GameEngine.window);
        imguiApp.initialize();
        soundSystem = new SoundSystem();

        player = new Player(Player.cameraX, Player.cameraY, Player.cameraZ, playerTexture, false);
        projectile = new Projectile(MapInfoParser.playerx,MapInfoParser.playery,0.001f,0, playerTexture);
        character = new NPC(12,12,1,2);
        mouseUtils = new MouseUtils(GameEngine.window);

        pistol = new Weapon("Pistol", 3, 20,1,1);
        rifle = new Weapon("Rifle", 3, 20,1,1);
        shotgun = new Weapon("Shotgun", 30, 20,4,5);

    }
    public static void loadSounds(){
        sampleSound= soundSystem.loadSound("resources/sound/Sample.wav");
    }

    public static void loadShaders(){
        fogShaderProgram = ShaderUtils.createShaderProgram(FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_vertex.glsl"), FileUtils.getCurrentWorkingDirectory("resources/shaders/fog/fog_fragment.glsl"));
    }

    public static void loadlua(){
        luaEngine = new LuaEngine();
        functionManager = new LuaManager(luaEngine.getGlobals());

    }
}