package com.toxicrain.gui;

import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiBuilder {
    private static final Map<String, ImFont> fonts = new HashMap<>();
    private static ImFont currentFont;

    public static void setFont(String alias, String fontPath, float fontSize) {
        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fontAtlas = io.getFonts();
        ImFont font = fontAtlas.addFontFromFileTTF(fontPath, fontSize);
        if (font != null) {
            fonts.put(alias, font);
        }
    }

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
    public GuiBuilder setPos(float x, float y){
        ImGui.setCursorPos(x, y);
        return this;
    }


    public GuiBuilder pushFont(String alias) {
        ImFont font = fonts.get(alias);
        if (font != null) {
            ImGui.pushFont(font);
            currentFont = font;
        }
        return this;
    }

    public GuiBuilder popFont() {
        if (currentFont != null) {
            ImGui.popFont();
            currentFont = null;
        }
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

    public GuiBuilder addButtonCentered(String label, Runnable onClick, float yOffset, float paddingWidth, float paddingHeight) {
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float textWidth = ImGui.calcTextSize(label).x;
        float buttonWidth = textWidth + paddingWidth; // Ensure padding allows for a nice button size

        ImGui.setCursorPos((screenWidth - buttonWidth) / 2, yOffset);
        if (ImGui.button(label, buttonWidth, 30 + paddingHeight)) {
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
