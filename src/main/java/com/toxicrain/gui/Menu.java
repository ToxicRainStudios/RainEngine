package com.toxicrain.gui;

import com.toxicrain.artifacts.animation.Animation;
import com.toxicrain.core.Logger;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.toxicrain.core.GameEngine.windowManager;
import static org.lwjgl.glfw.GLFW.*;

public class Menu {

    private static TextEngine textEngine;
    public static Font font;

    // Create buttons for the menu
    static AWTButton startButton = new AWTButton(0, 0, 200, 50, "Start Game");  // Centered at (0, 0)
    static AWTButton optionsButton = new AWTButton(-10, -10, 200, 50, "Options"); // Adjusted Y to center options button
    static AWTButton exitButton = new AWTButton(10, -20, 200, 50, "Exit");      // Adjusted Y to center exit button

    private static boolean inOptionsMenu = false;

    // Add an Animation for the background or button hover effect
    private static Animation backgroundAnimation;

    public static void initializeMenu() throws IOException, FontFormatException {
        // Load the font and create TextEngine
        font = Font.createFont(Font.TRUETYPE_FONT, new File(FileUtils.getCurrentWorkingDirectory("resources/fonts") + "/Perfect DOS VGA 437.ttf")).deriveFont(24f);
        textEngine = new TextEngine(font, 1);

        // Initialize the background animation (assuming you have a sprite sheet for the background)
        backgroundAnimation = new Animation(
                FileUtils.getCurrentWorkingDirectory("resources/sprites/SpiderSlash.png"), // Path to sprite sheet
                100, // Width of each frame
                50, // Height of each frame
                6, // Number of frames in the sprite sheet
                100, // Duration of each frame in milliseconds
                true // Loop the animation
        );

        backgroundAnimation.setPosition(0, -5); // Center the animation on screen
        backgroundAnimation.setScale(0.01f, 0.01f); // Center the animation on screen
        backgroundAnimation.setSize(SettingsInfoParser.windowWidth, SettingsInfoParser.windowHeight); // Set to window size
    }

    public static void updateMenu() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(windowManager.getWindow(), mouseX, mouseY);

        float windowWidth = SettingsInfoParser.windowWidth; // Your actual window width
        float windowHeight = SettingsInfoParser.windowHeight; // Your actual window height
        float adjustedMouseX = (float) mouseX[0] - (windowWidth / 2); // Convert to centered coordinates
        float adjustedMouseY = (windowHeight - (float) mouseY[0]) - (windowHeight / 2); // Invert and convert

        // Update the animation frames
        backgroundAnimation.update();

        // Check if the buttons are clicked
        if (startButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Start Button.");
            if (glfwGetMouseButton(windowManager.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                startButton.onClick();
            }
        }

        if (optionsButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Options Button.");
            if (glfwGetMouseButton(windowManager.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                optionsButton.onClick();
            }
        }

        if (exitButton.isMouseOver(adjustedMouseX, adjustedMouseY)) {
            System.out.println("Mouse is over Exit Button.");
            if (glfwGetMouseButton(windowManager.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                exitButton.onClick();
                glfwSetWindowShouldClose(windowManager.getWindow(), true);
            }
        }
    }

    public static void render(BatchRenderer batchRenderer) {
        // Render the background animation first
        backgroundAnimation.render(batchRenderer);

        // Render the menu title
        textEngine.render(batchRenderer, "Main Menu!", 0, 10); // Uncomment if needed

        // Render the buttons
        startButton.render(batchRenderer);
        optionsButton.render(batchRenderer);
        exitButton.render(batchRenderer);
    }
}
