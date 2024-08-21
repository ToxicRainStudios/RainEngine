package com.toxicrain.factories;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.artifacts.Projectile;
import com.toxicrain.artifacts.Weapon;
import com.toxicrain.core.GameEngine;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.util.MouseUtils;

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
}