package com.toxicrain.rainengine.gui;

import com.github.strubium.windowmanager.imgui.GuiBuilder;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.opengl.GL11;

public class SolverGuiReg {


    int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
            ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar |
            ImGuiWindowFlags.NoBackground;


    public void drawDebugInfo() {
        // Initialize the builder
        GuiBuilder builder = new GuiBuilder();

        // Begin Debug Window
        builder.beginWindow("Debug Info", windowFlags)
                .addText("Debug")

                // FPS Counter
                .addText("FPS" + ": " + ImGui.getIO().getFramerate())

                // Memory Usage
                .addText(String.format("Memory"+ ": %d MB / %d MB",
                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024),
                        Runtime.getRuntime().totalMemory() / (1024 * 1024)))

                // OpenGL Version
                .addText("OpenGL" + ": " + GL11.glGetString(GL11.GL_VERSION))

                // End Window
                .endWindow();
    }
}
