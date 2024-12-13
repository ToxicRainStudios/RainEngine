package com.toxicrain.gui;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.util.List;

public class GuiBuilder {

    public GuiBuilder beginWindow(String name) {
        return beginWindow(name, 0); // Default flags as 0
    }

    public GuiBuilder beginWindow(String name, int flags) {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
        ImGui.begin(name, flags);
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

    public GuiBuilder addComboBox(String label, ImInt selectedIndex, List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Options for combo box cannot be null or empty.");
        }

        String[] optionsArray = options.toArray(new String[0]);

        if (ImGui.beginCombo(label, optionsArray[selectedIndex.get()])) {
            for (int i = 0; i < optionsArray.length; i++) {
                boolean selected = (i == selectedIndex.get());
                if (ImGui.selectable(optionsArray[i], selected)) {
                    selectedIndex.set(i);
                }
            }
            ImGui.endCombo();
        }

        return this;
    }
    public GuiBuilder addSlider(String label, ImFloat value, float minValue, float maxValue, String format, float maxWidth) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        ImGui.setCursorPos((screenWidth - maxWidth) / 2, ImGui.getCursorPosY());
        ImGui.pushItemWidth(maxWidth); // Set max width for the slider
        if (ImGui.sliderFloat(label, value.getData(), minValue, maxValue, format)) {
            // Handle value change if needed
        }
        ImGui.popItemWidth(); // Reset item width after
        return this;
    }

    // New Method: addFloatInput for float values
    public GuiBuilder addFloatInput(String label, ImFloat value, float maxWidth) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        ImGui.setCursorPos((screenWidth - maxWidth) / 2, ImGui.getCursorPosY());
        ImGui.pushItemWidth(maxWidth); // Set max width for the input field
        if (ImGui.inputFloat(label, value)) {
            // Handle value change if needed
        }
        ImGui.popItemWidth(); // Reset item width after
        return this;
    }

    public GuiBuilder addTextCentered(String text, float yOffset) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float textWidth = ImGui.calcTextSize(text).x;
        ImGui.setCursorPos((screenWidth - textWidth) / 2, yOffset);
        ImGui.text(text);
        return this;
    }

    public GuiBuilder addButtonCentered(String label, Runnable onClick, float yOffset, float buttonWidth) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
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
