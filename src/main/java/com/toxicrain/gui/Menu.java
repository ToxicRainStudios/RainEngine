package com.toxicrain.gui;

import com.toxicrain.artifacts.animation.Animation;
import com.toxicrain.core.RainLogger;
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
        backgroundAnimation.setSize(SettingsInfoParser.getInstance().windowWidth, SettingsInfoParser.getInstance().windowHeight); // Set to window size
    }

    public static void updateMenu() {
        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(windowManager.getWindow(), mouseX, mouseY);



        // Update the animation frames
        backgroundAnimation.update();
    }

    public static void render(BatchRenderer batchRenderer) {
        // Render the background animation first
        backgroundAnimation.render(batchRenderer);

        // Render the menu title
        textEngine.render(batchRenderer, "Main Menu!", 0, 10); // Uncomment if needed
    }
}
