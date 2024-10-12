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
    private static ButtonEngine startButton;
    private static ButtonEngine optionsButton;
    private static ButtonEngine exitButton;

    private static boolean inOptionsMenu = false;

    public static void initializeMenu() throws IOException, FontFormatException {
        // Load the font and create TextEngine
        font = Font.createFont(Font.TRUETYPE_FONT, new File(FileUtils.getCurrentWorkingDirectory("resources/fonts") + "/Perfect DOS VGA 437.ttf")).deriveFont(24f);
        textEngine = new TextEngine(font, 1);
        
        // Initialize buttons for the main menu
        startButton = new ButtonEngine(textEngine, "Start Game", -10, 0);
        optionsButton = new ButtonEngine(textEngine, "Options", -10, 5);
        exitButton = new ButtonEngine(textEngine, "Exit", -10, 10);
    }

    public static void updateMenu() {

        // In your game loop or main update/render function
        float[] mousePosition = GameFactory.mouseUtils.getMousePosition();
        float mouseX = mousePosition[0];
        float mouseY = mousePosition[1];
        boolean mouseClick = GameFactory.mouseUtils.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT); // Implement this based on your input handling

        // Update the button
        startButton.update(mouseX, mouseY, mouseClick);


        // Handle button clicks
        if (startButton.isClicked()) {
            Logger.printLOG("hi");
        }
    }

    public static void render(BatchRenderer batchRenderer) {
        // Render the menu title
        textEngine.render(batchRenderer, "Main Menu", -10, -5);

        // Render buttons
        startButton.render(batchRenderer);
        optionsButton.render(batchRenderer);
        exitButton.render(batchRenderer);


        if (inOptionsMenu) {
            renderOptionsMenu(batchRenderer);
        }
    }

    private static void startGame() {
        System.out.println("Start Game button clicked!");
        // Implement game start logic
    }

    private static void openOptions() {
        System.out.println("Options button clicked!");
        inOptionsMenu = true; // You can add an options menu screen here
    }

    private static void exitGame() {
        System.out.println("Exit button clicked!");
        System.exit(0); // Exit the application
    }

    private static void renderOptionsMenu(BatchRenderer batchRenderer) {
        // Render options menu if needed
        textEngine.render(batchRenderer, "Options Menu", 100, 150);
        // Add options rendering here
    }
}
