package com.toxicrain.rainengine.core.eventbus;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.window.WindowManager;
import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.GameLoader;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.eventbus.events.DrawMapEvent;
import com.toxicrain.rainengine.core.eventbus.events.GameUpdateEvent;
import com.toxicrain.rainengine.core.eventbus.events.KeyPressEvent;
import com.toxicrain.rainengine.core.eventbus.events.ScrollEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.InitLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.ManagerLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.PostInitLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.PreInitLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.RenderGuiEvent;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.PaletteInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.core.lua.LuaManager;
import com.toxicrain.rainengine.core.registries.manager.NPCManager;
import com.toxicrain.rainengine.core.registries.manager.ProjectileManager;
import com.toxicrain.rainengine.core.registries.tiles.Tile;
import com.toxicrain.rainengine.core.resources.ResourceManager;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.sound.SoundInfo;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import org.lwjgl.glfw.GLFWScrollCallback;

import static com.toxicrain.rainengine.core.GameEngine.drawMap;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.opengl.GL11.*;


/**
 * RainBusListener where we listen to the EventBus for RainEngine.
 * Functionally is done here, and Events are posted in GameEngine
 */
public class RainBusListener {

    public static void addEventListeners(){

        SmeagleBus.getInstance().listen(PreInitLoadEvent.class)
                .subscribe(event -> {
                    RainLogger.RAIN_LOGGER.debug("Looking for: {}", GameInfoParser.gameMainClass);
                    GameLoader.loadAndInitGame(GameInfoParser.gameMainClass);
                });


        SmeagleBus.getInstance().listen(InitLoadEvent.class)
                .subscribe(event -> {
                    RainLogger.RAIN_LOGGER.info("Loading Lua");
                    GameFactory.loadLua();
                    LuaManager.categorizeScripts("resources/scripts/");
                    LuaManager.executeInitScripts();
                    Tile.combineTouchingAABBs();

                    ResourceManager.register(TextureInfo.class, TextureSystem::loadTexture);
                    ResourceManager.register(SoundInfo.class, SoundSystem::loadSound);

                    GameEngine.windowManager = new WindowManager((int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), true);

                    RainLogger.RAIN_LOGGER.info("Creating Game Window");
                    GameEngine.windowManager.createWindow(GameInfoParser.defaultWindowName, SettingsInfoParser.getInstance().getVsync());

                    // Fire the KeyPressEvent when a key is pressed
                    glfwSetKeyCallback(GameEngine.windowManager.window, (windowHandle, key, scancode, action, mods) -> {
                        SmeagleBus.getInstance().post(new KeyPressEvent(key, action));
                    });

                    // Create and set the scroll callback
                    glfwSetScrollCallback(GameEngine.windowManager.window, new GLFWScrollCallback() {
                        @Override
                        public void invoke(long window, double xoffset, double yoffset) {
                            SmeagleBus.getInstance().post(new ScrollEvent((float) xoffset, (float) yoffset));
                        }
                    });

                    RainLogger.RAIN_LOGGER.info("Creating Textures");
                    TextureSystem.initTextures();

                    RainLogger.RAIN_LOGGER.info("Loading Keybinds");
                    KeyInfoParser.loadKeyInfo();

                    // Set the "background" color
                    glClearColor(0, 0, 0, 0);

                    // Set up the projection matrix with FOV of 90 degrees
                    glMatrixMode(GL_PROJECTION);
                    glLoadMatrixf(GameEngine.createPerspectiveProjectionMatrix(SettingsInfoParser.getInstance().getFOV(), SettingsInfoParser.getInstance().getWindowWidth() / SettingsInfoParser.getInstance().getWindowHeight(), 1.0f, 100.0f));

                    GameFactory.load();

                    RainLogger.RAIN_LOGGER.info("Loading ImGUI");
                    GameFactory.loadImgui();

                    RainLogger.RAIN_LOGGER.info("Loading Fonts");
                    GameFactory.loadFonts();

                    RainLogger.RAIN_LOGGER.info("Loading Map Palette");
                    PaletteInfoParser.loadTextureMappings();

                    // Set the viewport size
                    glViewport(0, 0, (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight());
                });

        SmeagleBus.getInstance().listen(PostInitLoadEvent.class)
                .subscribe(event -> {
                    RainLogger.RAIN_LOGGER.info("Initializing SoundSystem");
                    GameFactory.loadSounds();

                    RainLogger.RAIN_LOGGER.info("Loading Shaders");
                    GameFactory.loadShaders();

                    LuaManager.executePostInitScripts();

                    GameFactory.setupGUIs();

                    RainLogger.RAIN_LOGGER.info("Loading Lang");
                    GameFactory.loadLang();


                    //"COMBAT" is the normal track, "PANIC" is the low health track, "CALM" is the quiet track
                    GameFactory.musicManager.setStartingSound("CALM0");
                    GameFactory.musicManager.start();
                    GameFactory.musicManager.setNextTrack("CALM1");
                });

        SmeagleBus.getInstance().listen(ManagerLoadEvent.class)
                .subscribe(event -> {
                    GameFactory.projectileManager = new ProjectileManager();
                    GameFactory.npcManager = new NPCManager();
                    GameFactory.guiManager = new GuiManager();
                });

        SmeagleBus.getInstance().listen(KeyPressEvent.class)
                .subscribe(event -> {
                    int keycode = event.keyCode;
                    if (KeyMap.keyBinds.containsKey(keycode)) {
                        KeyMap.keyBinds.get(keycode).run();
                    }
                });

        SmeagleBus.getInstance().listen(GameUpdateEvent.class)
                .subscribe(event -> {

                    float deltaTime = DeltaTimeUtil.getDeltaTime();

                    GameFactory.player.update(deltaTime);

                    for (int engineFrames = 30; engineFrames >= 0; engineFrames--) {

                        GameFactory.npcManager.update(deltaTime);

                        GameFactory.projectileManager.update(deltaTime);

                    }
                    LuaManager.executeTickScripts();
                });

        SmeagleBus.getInstance().listen(DrawMapEvent.class)
                .subscribe(event -> {
                    drawMap(event.getBatchRenderer());
                });

        SmeagleBus.getInstance().listen(RenderGuiEvent.class)
                .subscribe(event -> {
                    GameFactory.guiManager.render();
                    LuaManager.executeAllImguiScripts();
                });

        SmeagleBus.getInstance().listen(ScrollEvent.class)
                .subscribe(event -> {
                    GameFactory.player.scrollOffset = event.yOffeset;
                });
    }

}
