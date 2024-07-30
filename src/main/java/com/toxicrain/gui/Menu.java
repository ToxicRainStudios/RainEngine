package com.toxicrain.gui;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.ButtonEngine;
import com.toxicrain.util.TextEngine;



public class Menu {

    private static TextEngine textEngine;
    private static TextEngine textPart1;
    private static TextEngine textPart2;
    private static TextEngine textPart3;

    private static ButtonEngine button;

    public static void initializeMenu() {
        textEngine = new TextEngine("sigma tropism", 0, -23);
        textPart1 = new TextEngine("sigma", 10, -23);
        textPart2 = new TextEngine("theta", -10, -23);
        textPart3 = new TextEngine("the sigma begins", 0, -18);

        // Initialize the button with a default texture type, position (x, y)
        button = new ButtonEngine("Click Me", -20, -23);
    }

    public static void updateMenu(float mouseX, float mouseY, boolean mouseClick) {
        // Update the button's state based on mouse position and click status
        button.update(mouseX, mouseY, mouseClick);

        // Handle button click event
        if (button.isClicked()) {
            // Perform the action you want when the button is clicked
            System.out.println("Button clicked!");
        }
    }

    public static void render(BatchRenderer batchRenderer) {
        // Render text
        textEngine.render(batchRenderer);
        textPart1.render(batchRenderer);
        textPart2.render(batchRenderer);
        textPart3.render(batchRenderer);

        // Render button
        button.render(batchRenderer);
    }
}
