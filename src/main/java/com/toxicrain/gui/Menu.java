package com.toxicrain.gui;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.ButtonEngine;
import com.toxicrain.util.TextEngine;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class Menu {

    private static TextEngine textEngine;
    public static Font font;
    private static ButtonEngine button;

    public static void initializeMenu() throws IOException, FontFormatException {
        font = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\hudso\\Downloads\\perfect_dos_vga_437/Perfect DOS VGA 437.ttf")).deriveFont(24f);
        textEngine = new TextEngine(font, 1);


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
        textEngine.render(batchRenderer, "Amongus", 1,2);


        // Render button
        button.render(batchRenderer);
    }
}
