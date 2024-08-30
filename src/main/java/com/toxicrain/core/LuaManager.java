package com.toxicrain.core;

import com.toxicrain.util.FileUtils;
import org.luaj.vm2.*;

import static com.toxicrain.factories.GameFactory.luaEngine;

public class LuaManager {
    private Globals globals;

    public LuaManager(Globals globals) {
        this.globals = globals;
        registerFunctions();
    }

    private void registerFunctions() {
        globals.set("log", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Logger.printLOG(arg.tojstring());
                return arg;
            }
        });

        globals.set("error", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Logger.printERROR(arg.tojstring());
                return arg;
            }
        });

        globals.set("add", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue a, LuaValue b) {
                if (a.isnumber() && b.isnumber()) {
                    double sum = a.todouble() + b.todouble();
                    return LuaValue.valueOf(sum);
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        // Add more functions as needed
    }

    public static void loadScript(String scriptPath) {
        try {
            Globals globals = luaEngine.getGlobals();
            String script = FileUtils.readFile(scriptPath);  // Read the script content
            LuaValue chunk = globals.load(script, scriptPath);  // Load the script from content
            chunk.call();  // Execute the script
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
