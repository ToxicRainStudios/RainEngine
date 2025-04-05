package com.toxicrain.core.lua;

import lombok.Getter;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

@Getter
public class LuaEngine {

    private final Globals globals;

    public LuaEngine() {
        globals = JsePlatform.standardGlobals();  // Use Globals to manage Lua environment
    }
}


