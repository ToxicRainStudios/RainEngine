package com.toxicrain.gui;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

import java.util.List;

public class GuiBuilder {

    public GuiBuilder beginWindow(String name) {
        ImGui.begin(name);
        return this;
    }

    public GuiBuilder endWindow() {
        ImGui.end();
        return this;
    }

    public GuiBuilder addButton(String label, Runnable onClick) {
        if (ImGui.button(label)) {
            onClick.run();
        }
        return this;
    }

    public GuiBuilder addText(String text) {
        ImGui.text(text);
        return this;
    }

    public GuiBuilder addCheckbox(String label, ImBoolean value) {
        ImGui.checkbox(label, value);
        return this;
    }

    // Combo box support
    public GuiBuilder addComboBox(String label, ImInt selectedIndex, List<String> options) {
        // Convert the List of options to an array of Strings (as required by ImGui)
        String[] optionsArray = options.toArray(new String[0]);

        // Create the combo box
        if (ImGui.beginCombo(label, optionsArray[selectedIndex.get()])) {
            for (int i = 0; i < optionsArray.length; i++) {
                boolean selected = (i == selectedIndex.get());
                if (ImGui.selectable(optionsArray[i], selected)) {
                    selectedIndex.set(i); // Update the selected index
                }
            }
            ImGui.endCombo();
        }

        return this;
    }

    public GuiBuilder beginWindow(String name, int flags) {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
        ImGui.begin(name, flags);
        return this;
    }

    public GuiBuilder addTextCentered(String text, float yOffset) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float textWidth = ImGui.calcTextSize(text).x;
        ImGui.setCursorPos((screenWidth - textWidth) / 2, yOffset);
        ImGui.text(text);
        return this;
    }

    public GuiBuilder addButtonCentered(String label, Runnable onClick, float yOffset) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float buttonWidth = 100;
        ImGui.setCursorPos((screenWidth - buttonWidth) / 2, yOffset);
        if (ImGui.button(label, buttonWidth, 30)) {
            onClick.run();
        }
        return this;
    }

    public GuiBuilder addTextAtPosition(String text, float x, float y) {
        ImGui.setCursorPos(x, y);
        ImGui.text(text);
        return this;
    }

    public void render() {
        ImGui.render();
    }

}
