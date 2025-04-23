package com.toxicrain.rainengine.gui;

import imgui.ImGui;

public class GuiLuaWrapper {

    public void luaBeginWindow(String title) {
        ImGui.begin(title);
    }

    public void luaEndWindow() {
        ImGui.end();
    }

    public void luaCreateLabel(String text) {
        ImGui.text(text);
    }

    public void luaSetWindowSize(int width, int height) {
        ImGui.setWindowSize(width, height);
    }

    public void luaSetDisabled() {
        ImGui.beginDisabled();
    }

    public void luaSetEnabled() {
        ImGui.endDisabled();
    }

    public boolean luaCreateButton(String label) {
        return ImGui.button(label);
    }
    public boolean luaCreateCheckbox(String label, boolean toboolean) {
        return ImGui.checkbox(label, toboolean);
    }

    private final float[] currentColor = {1.0f, 0.5f, 0.0f, 1.0f}; // Initial color

    // Method to create a color picker
    public void luaCreateColorPicker(String label) {
        // Create the color picker in the ImGui window
        if (ImGui.colorPicker4(label, currentColor)) {
            // Logic to handle color change can be placed here if needed
        }
    }
}
