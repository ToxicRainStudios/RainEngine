package com.toxicrain.rainengine.factories;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.toxicrain.rainengine.artifacts.*;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.LangHelper;
import com.github.strubium.windowmanager.window.InputUtils;

import static com.toxicrain.rainengine.core.GameEngine.windowManager;

public class GameFactory {
    public static Player player;
    public static GuiManager guiManager;
    public static InputUtils inputUtils;
    public static LangHelper langHelper;

    public static void load() {

        // Load player using the atlas region
        player = new Player(new Resource("rainengine:playertexture"));

        inputUtils = new InputUtils(windowManager.window);
    }

    public static void loadFonts() {
        // GuiBuilder.setFont("dos", FileUtils.getCurrentWorkingDirectory("resources/fonts/Perfect DOS VGA 437.ttf"), 30);
    }
}
