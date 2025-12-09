package com.toxicrain.rainengine.factories;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.toxicrain.rainengine.artifacts.*;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.gui.editor.ImMapEditorMenu;
import com.toxicrain.rainengine.core.LangHelper;
import com.toxicrain.rainengine.core.lua.LuaManager;
import com.toxicrain.rainengine.core.lua.LuaEngine;
import com.toxicrain.rainengine.gui.GuiLuaWrapper;
import com.toxicrain.rainengine.gui.GuiReg;
import com.toxicrain.rainengine.util.InputUtils;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;

import static com.toxicrain.rainengine.core.GameEngine.windowManager;

public class GameFactory {

    public static ImguiHandler imguiApp;
    public static GuiReg guiReg;

    public static Player player;
    public static GuiManager guiManager;
    public static InputUtils inputUtils;
    public static GuiLuaWrapper guiLuaWrapper;
    public static LuaManager functionManager;
    public static LangHelper langHelper;

    public static void load() {

        // Load player using the atlas region
        player = new Player(new Resource("playerTexture"), false);

        inputUtils = new InputUtils(windowManager);
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
        guiLuaWrapper = new GuiLuaWrapper();
        functionManager = new LuaManager(LuaEngine.getInstance().getGlobals());
    }

    public static void setupGUIs() {
        ImGui.getIO().setConfigFlags(ImGui.getIO().getConfigFlags() | ImGuiConfigFlags.DockingEnable);


        guiReg = new GuiReg();
        guiManager.registerGUI("MainMenu", (v) -> guiReg.drawMainMenu());
        guiManager.registerGUI("Settings", (v) -> guiReg.drawSettingsMenu());
        guiManager.registerGUI("Keybinds", (v) -> guiReg.drawKeyBindingInfo());
        guiManager.registerGUI("Inventory", (v) -> guiReg.drawInventory());
        guiManager.registerGUI("FileEditor", (v) -> guiReg.drawFileEditorUI());
        guiManager.registerGUI("Console", (v) -> guiReg.drawConsole());
        guiManager.registerGUI("Debug", (v) -> guiReg.drawDebugInfo());
        guiManager.registerGUI("DeathScreen", (v) -> guiReg.drawDeathScreen());
        guiManager.registerGUI("MapEditor", (v) -> ImMapEditorMenu.getInstance().draw());
        //guiManager.addActiveGUI("MapEditor");
        //guiManager.addActiveGUI("Debug");
        //guiManager.addActiveGUI("Keybinds");
        guiManager.addActiveGUI("MainMenu");
    }
}
