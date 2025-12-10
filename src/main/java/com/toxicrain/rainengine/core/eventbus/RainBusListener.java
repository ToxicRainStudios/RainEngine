package com.toxicrain.rainengine.core.eventbus;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.window.WindowManager;
import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.GameLoader;
import com.toxicrain.rainengine.core.LangHelper;
import com.toxicrain.rainengine.core.eventbus.events.load.LangLoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.LoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.load.sound.SoundSystemLoadEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.eventbus.events.DrawMapEvent;
import com.toxicrain.rainengine.core.eventbus.events.GameUpdateEvent;
import com.toxicrain.rainengine.core.eventbus.events.KeyPressEvent;
import com.toxicrain.rainengine.core.eventbus.events.ScrollEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.RenderGuiEvent;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.PaletteInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.core.lua.LuaManager;
import com.toxicrain.rainengine.core.registries.manager.NPCManager;
import com.toxicrain.rainengine.core.registries.manager.ProjectileManager;
import com.toxicrain.rainengine.core.registries.manager.TriggerManager;
import com.toxicrain.rainengine.core.registries.tiles.Tile;
import com.toxicrain.rainengine.core.resources.ResourceManager;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.sound.SoundInfo;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.sound.music.MusicManager;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import com.toxicrain.rainengine.util.FileUtils;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.file.Path;
import java.util.Locale;

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

        SmeagleBus.getInstance().listen(LoadEvent.class)
                .subscribe(event -> {
                    if(event.loadEventStage == LoadEvent.LoadEventStage.PRE){
                        RainLogger.RAIN_LOGGER.debug("Looking for: {}", GameInfoParser.getInstance().gameMainClass);
                        GameLoader.loadAndInitGame(GameInfoParser.getInstance().gameMainClass);
                    }
                });


        SmeagleBus.getInstance().listen(LoadEvent.class)
                .subscribe(event -> {
                    if(event.loadEventStage == LoadEvent.LoadEventStage.ININT){

                        RainLogger.RAIN_LOGGER.info("Loading Lua");
                        GameFactory.loadLua();
                        LuaManager.categorizeScripts("resources/scripts/");
                        LuaManager.executeInitScripts();
                        Tile.combineTouchingAABBs();

                        ResourceManager.register(SoundInfo.class, SoundSystem::loadSound);

                        GameEngine.windowManager = new WindowManager((int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), SettingsInfoParser.getInstance().getFullscreen());

                        RainLogger.RAIN_LOGGER.info("Creating Game Window");
                        GameEngine.windowManager.createWindow(GameInfoParser.getInstance().defaultWindowName, SettingsInfoParser.getInstance().getVsync());

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
                        TextureSystem.getInstance().initTextures();

                        RainLogger.RAIN_LOGGER.info("Loading Keybinds");
                        KeyInfoParser.getInstance().loadKeyInfo();

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
                        PaletteInfoParser.getInstance().loadTextureMappings();

                        // Set the viewport size
                        glViewport(0, 0, (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight());
                    }
                    });

        SmeagleBus.getInstance().listen(LoadEvent.class)
                .subscribe(event -> {
                    if (event.loadEventStage == LoadEvent.LoadEventStage.POST) {
                        RainLogger.RAIN_LOGGER.info("Initializing SoundSystem");
                        SoundSystem soundSystem = SoundSystem.getInstance(); // Construct singleton
                        soundSystem.postLoad();

                        RainLogger.RAIN_LOGGER.info("Loading Shaders");
                        GameFactory.loadShaders();

                        LuaManager.executePostInitScripts();

                        GameFactory.setupGUIs();

                        SmeagleBus.getInstance().post(new LangLoadEvent(SettingsInfoParser.getInstance().getLanguage()));


                        //"COMBAT" is the normal track, "PANIC" is the low health track, "CALM" is the quiet track
                        MusicManager.getInstance().setStartingSound("CALM0");
                        MusicManager.getInstance().start();
                        MusicManager.getInstance().setNextTrack("CALM1");
                    }
                });

        SmeagleBus.getInstance().listen(LoadEvent.class)
                .subscribe(event -> {
                    if (event.loadEventStage == LoadEvent.LoadEventStage.MANAGER) {
                        GameFactory.guiManager = new GuiManager();
                }
                });

        SmeagleBus.getInstance().listen(KeyPressEvent.class)
                .subscribe(event -> {
                    int keycode = event.keyCode;
                    if (KeyMap.keyBinds.containsKey(keycode)) {
                        KeyMap.keyBinds.get(keycode).run();
                    }
                });

        SmeagleBus.getInstance().listen(LangLoadEvent.class)
                .subscribe(event -> {
                    RainLogger.RAIN_LOGGER.info("Using Lang: {}", event.langTag);

                    GameFactory.langHelper = new LangHelper("raiengine", Path.of(FileUtils.getCurrentWorkingDirectory("resources/lang")), Locale.forLanguageTag(event.correctedLangTag));

                    RainLogger.RAIN_LOGGER.info(GameFactory.langHelper.get("greeting"));
                });

        SmeagleBus.getInstance().listen(SoundSystemLoadEvent.class)
            .subscribe(event -> {
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
            });

        SmeagleBus.getInstance().listen(GameUpdateEvent.class)
                .subscribe(event -> {
                    // Update camera with player position
                    event.camera.setPosition(new Vector3f(
                            GameFactory.player.getPosition().x,
                            GameFactory.player.getPosition().y,
                            GameFactory.player.getPosition().z
                    ));
                    event.camera.setRotation(new Vector3f(0, 35, 0));

                    });


        SmeagleBus.getInstance().listen(GameUpdateEvent.class)
                .subscribe(event -> {

                    double deltaTime = DeltaTimeUtil.getDeltaTime();

                    GameFactory.player.update(deltaTime);

                    TriggerManager.getInstance().update(GameFactory.player.getPosition());

                    NPCManager.getInstance().update(deltaTime);

                    ProjectileManager.getInstance().update(deltaTime);

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
                    GameFactory.player.scrollOffset = event.yOffset;
                });
    }

}
