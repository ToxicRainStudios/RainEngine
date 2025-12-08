package com.toxicrain.rainengine.core.lua;

import com.toxicrain.instanceable.BaseInstanceable;
import lombok.Getter;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

@Getter
public class LuaEngine extends BaseInstanceable<LuaEngine> {

    private final Globals globals;

    public static LuaEngine getInstance() {
        return BaseInstanceable.getInstance(LuaEngine.class);
    }

    private LuaEngine() {
        globals = JsePlatform.standardGlobals();  // Use Globals to manage Lua environment
    }
}


