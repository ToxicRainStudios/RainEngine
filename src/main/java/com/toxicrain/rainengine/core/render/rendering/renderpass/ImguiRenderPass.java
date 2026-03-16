package com.toxicrain.rainengine.core.render.rendering.renderpass;

import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.eventbus.events.lua.ExecuteAllLuaScripts;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.core.render.rendering.RenderPass;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.gui.ImguiSystem;
import com.github.strubium.smeaglebus.eventbus.SmeagleBus;

public class ImguiRenderPass implements RenderPass {

    @Override
    public void render(BatchRenderer batchRenderer, Camera camera) {
        // Start a new ImGui frame
        ImguiSystem.getInstance().getImguiApp().newFrame();

        // Render in-game GUI
        GameFactory.guiManager.render();

        // Execute any Lua scripts that should run at this stage
        SmeagleBus.getInstance().post(new ExecuteAllLuaScripts(ExecuteAllLuaScripts.EventStage.IMGUI));

        // Render the ImGui frame
        ImguiSystem.getInstance().getImguiApp().render();
    }
}