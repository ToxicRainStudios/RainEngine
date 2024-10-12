package com.toxicrain.gui;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Menu {

    private static TextEngine textEngine;
    public static Font font;

    private static boolean inOptionsMenu = false;

    public static void initializeMenu() throws IOException, FontFormatException {
        // Load the font and create TextEngine
        font = Font.createFont(Font.TRUETYPE_FONT, new File(FileUtils.getCurrentWorkingDirectory("resources/fonts") + "/Perfect DOS VGA 437.ttf")).deriveFont(24f);
        textEngine = new TextEngine(font, 1);

    }

    public static void updateMenu() {


    }

    public static void render(BatchRenderer batchRenderer) {
        // Render the menu title
        textEngine.render(batchRenderer, "Main Menu", -10, -5);
    }
}
