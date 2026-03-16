package com.toxicrain.rainengine.gui;

import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.github.strubium.windowmanager.window.WindowManager;
import com.toxicrain.instanceable.BaseInstanceable;
import lombok.Getter;

public class ImguiSystem extends BaseInstanceable<ImguiSystem> {
    private @Getter ImguiHandler imguiApp;

    public static ImguiSystem getInstance() {
        return BaseInstanceable.getInstance(ImguiSystem.class);
    }

    public void initialize(WindowManager windowManager) {
        imguiApp = new ImguiHandler(windowManager);
        imguiApp.initialize("#version 130");
    }
}

