package com.toxicrain.gui;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.toxicrain.core.GameEngine.window;
import static org.lwjgl.glfw.GLFW.*;

public class Menu {

    private static TextEngine textEngine;
    public static Font font;

    // Create buttons for the menu
    static AWTButton startButton = new AWTButton(0, 0, 200, 50, "Start Game");  // Centered at (0, 0)
    static AWTButton optionsButton = new AWTButton(-10, -10, 200, 50, "Options"); // Adjusted Y to center options button
    static AWTButton exitButton = new AWTButton(10, -20, 200, 50, "Exit");      // Adjusted Y to center exit button

    private static boolean inOptionsMenu = false;

    public static void initializeMenu() throws IOException, FontFormatException {
        // Load the font and create TextEngine
        font = Font.createFont(Font.TRUETYPE_FONT, new File(FileUtils.getCurrentWorkingDirectory("resources/fonts") + "/Perfect DOS VGA 437.ttf")).deriveFont(24f);
        textEngine = new TextEngine(font, 1);
    }

    public static void updateMenu() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(window, mouseX, mouseY);

        float windowWidth = SettingsInfoParser.windowWidth; // Your actual window width
        float windowHeight = SettingsInfoParser.windowHeight; // Your actual window height
        float adjustedMouseX = (float) mouseX[0] - (windowWidth / 2); // Convert to centered coordinates
        float adjustedMouseY = (windowHeight - (float) mouseY[0]) - (windowHeight / 2); // Invert and convert
        
        // Check if the buttons are clicked
        if (startButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Start Button.");
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                startButton.onClick();
            }
        }

        if (optionsButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Options Button.");
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                optionsButton.onClick(); // Use onClick instead of onMouseClick
            }
        }

        if (exitButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Exit Button.");
            if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                exitButton.onClick();
                glfwSetWindowShouldClose(window, true);
            }
        }
    }

    public static void render(BatchRenderer batchRenderer) {
        // Render the menu title
        // textEngine.render(batchRenderer, "Main Menu", 0, 0); // Uncomment if needed

        // Render the buttons
        startButton.render(batchRenderer);
        optionsButton.render(batchRenderer);
        exitButton.render(batchRenderer);
    }
}
