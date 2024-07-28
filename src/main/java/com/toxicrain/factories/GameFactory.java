package com.toxicrain.factories;

import com.toxicrain.artifacts.NPC;
import com.toxicrain.artifacts.Player;
import com.toxicrain.artifacts.Projectile;
import com.toxicrain.core.GameEngine;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.gui.ImguiHandler;
import com.toxicrain.sound.SoundSystem;

import static com.toxicrain.util.TextureUtils.playerTexture;

public class GameFactory {

    private static ImguiHandler imguiApp;
    public static SoundSystem soundSystem;

    public static Player player;
    public static Projectile projectile;
    public static NPC character;

    public static void load(){
        imguiApp = new ImguiHandler(GameEngine.window);
        soundSystem = new SoundSystem();

        player = new Player(Player.cameraX, Player.cameraY, Player.cameraZ, playerTexture, false);
        projectile = new Projectile(MapInfoParser.playerx,MapInfoParser.playery,0.001f,0);
        character = new NPC(12,12,1,2);

    }
}