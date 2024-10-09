package com.toxicrain.core.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEngine {
    private final Globals globals;

    public LuaEngine() {
        globals = JsePlatform.standardGlobals();  // Use Globals to manage Lua environment
    }

    public Globals getGlobals() {
        return globals;
    }
}


