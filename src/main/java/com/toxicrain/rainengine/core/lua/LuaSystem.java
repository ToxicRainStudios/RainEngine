package com.toxicrain.rainengine.core.lua;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.gui.GuiLuaWrapper;
import lombok.Getter;

public class LuaSystem extends BaseInstanceable<LuaSystem> {
    @Getter private GuiLuaWrapper guiWrapper;
    @Getter private LuaManager luaManager;

    public static LuaSystem getInstance() {
        return BaseInstanceable.getInstance(LuaSystem.class);
    }

    public void initialize() {
        guiWrapper = new GuiLuaWrapper();
        luaManager = new LuaManager(LuaEngine.getInstance().getGlobals());
    }
}

