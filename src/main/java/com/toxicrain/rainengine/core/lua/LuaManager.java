package com.toxicrain.rainengine.core.lua;

import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.util.FileUtils;
import org.luaj.vm2.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.toxicrain.rainengine.factories.GameFactory.luaEngine;

public class LuaManager {
    private final Globals globals;
    private static final List<String> initScripts = new ArrayList<>();
    private static final List<String> postInitScripts = new ArrayList<>();
    private static final List<String> tickScripts = new ArrayList<>();
    private static final List<String> mapAutorunScripts = new ArrayList<>();
    private static final List<String> imguiScripts = new ArrayList<>();

    public LuaManager(Globals globals) {
        this.globals = globals;
        registerFunctions();
    }

    /**
     * Registers all the functions that can be used in a Lua file
     */
    private void registerFunctions() {
        globals.set("log", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                RainLogger.LUA_LOGGER.info(arg.tojstring());
                return arg;
            }
        });

        globals.set("error", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                RainLogger.RAIN_LOGGER.error(arg.tojstring());
                return arg;
            }
        });

        globals.set("power", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue base, LuaValue exp) {
                if (base.isnumber() && exp.isnumber()) {
                    return LuaValue.valueOf(Math.pow(base.todouble(), exp.todouble()));
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        globals.set("modulus", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue a, LuaValue b) {
                if (a.isnumber() && b.isnumber()) {
                    return LuaValue.valueOf(a.toint() % b.toint());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        globals.set("random", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue min, LuaValue max) {
                int result = (int) (Math.random() * (max.toint() - min.toint() + 1)) + min.toint();
                return LuaValue.valueOf(result);
            }
        });

        globals.set("format", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue format, LuaValue arg) {
                return LuaValue.valueOf(String.format(format.tojstring(), arg.tojstring()));
            }
        });

        globals.set("currentTimeMillis", new LuaFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });

        globals.set("getCurrentDateTime", new LuaFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(java.time.LocalDateTime.now().toString());
            }
        });

        globals.set("sleep", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue millis) {
                try {
                    Thread.sleep(millis.tolong());
                    return LuaValue.TRUE;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return LuaValue.FALSE;
                }
            }
        });

        globals.set("loadMap", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                try {
                    RainLogger.LUA_LOGGER.info("Loading Map Data");
                    MapInfoParser.getInstance().parseMapFile(String.valueOf(arg));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return arg;
            }
        });

        globals.set("isKeyPressed", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber(arg.toString()))) {
                    return LuaValue.TRUE;
                }
                return LuaValue.FALSE;
            }
        });

        globals.set("beginWindow", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue title) {
                GameFactory.guiLuaWrapper.luaBeginWindow(title.tojstring());
                return LuaValue.TRUE;
            }
        });
        globals.set("setDisabled", new LuaFunction() {
            @Override
            public LuaValue call() {
                GameFactory.guiLuaWrapper.luaSetDisabled();
                return LuaValue.TRUE;
            }
        });
        globals.set("setEnabled", new LuaFunction() {
            @Override
            public LuaValue call() {
                GameFactory.guiLuaWrapper.luaSetEnabled();
                return LuaValue.TRUE;
            }
        });

        globals.set("endWindow", new LuaFunction() {
            @Override
            public LuaValue call() {
                GameFactory.guiLuaWrapper.luaEndWindow();
                return LuaValue.TRUE;
            }
        });

        globals.set("setWindowSize", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue width, LuaValue height) {
                GameFactory.guiLuaWrapper.luaSetWindowSize(width.toint(), height.toint());
                return LuaValue.TRUE;
            }
        });

        globals.set("createLabel", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue text) {
                GameFactory.guiLuaWrapper.luaCreateLabel(text.tojstring());
                return LuaValue.TRUE;
            }
        });

        globals.set("createButton", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue label) {
                // Call the Java method and return the result as a Lua boolean
                boolean isPushed = GameFactory.guiLuaWrapper.luaCreateButton(label.tojstring());
                return LuaValue.valueOf(isPushed);
            }
        });

        globals.set("createCheckbox", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue label, LuaValue initialValue) {
                boolean isChecked = GameFactory.guiLuaWrapper.luaCreateCheckbox(label.tojstring(), initialValue.toboolean());
                return LuaValue.valueOf(isChecked);
            }
        });

        globals.set("createColorPicker", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue label) {
                // Call the Java method to create the color picker
                GameFactory.guiLuaWrapper.luaCreateColorPicker(label.tojstring());

                // After the color picker is used, retrieve the current color (if needed)
                // Update Lua state if necessary
                return LuaValue.TRUE; // Indicate success
            }
        });

        globals.set("fileExists", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.exists() ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("deleteFile", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.delete() ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("renameFile", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue oldPath, LuaValue newPath) {
                File oldFile = new File(oldPath.tojstring());
                File newFile = new File(newPath.tojstring());
                return oldFile.renameTo(newFile) ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("getFileSize", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.exists() ? LuaValue.valueOf(file.length()) : LuaValue.NIL;
            }
        });

        globals.set("getFileExtension", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf('.');
                return dotIndex >= 0 ? LuaValue.valueOf(fileName.substring(dotIndex + 1)) : LuaValue.NIL;
            }
        });

        globals.set("runScript", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue script) {
                loadScript(String.valueOf(script));
                return LuaValue.valueOf(String.valueOf(script));
            }
        });

        globals.set("runScriptCustomDir", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue script, LuaValue relativePath) {
                loadScript(String.valueOf(script), String.valueOf(relativePath));
                return LuaValue.valueOf(String.valueOf(script));
            }
        });

        globals.set("mergeFiles", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue file1, LuaValue file2, LuaValue outputFile) {
                String localDir = FileUtils.getCurrentWorkingDirectory("resources/scripts");
                File fileA = new File(localDir.concat( "/" + file1.tojstring()));
                File fileB = new File(localDir.concat("/" +file2.tojstring()));
                File outFile = new File(localDir.concat("/" + outputFile.tojstring()));

                if (!fileA.exists() || !fileB.exists()) {
                    return LuaValue.error("One or both input files do not exist.");
                }

                try {
                    // Read the contents of the first and second files
                    String contentA = FileUtils.readFile(fileA.getPath());
                    String contentB = FileUtils.readFile(fileB.getPath());

                    // Merge the contents
                    String mergedContent = contentA + contentB;

                    // Write the merged content back to the output file
                    FileUtils.writeFile(outFile.getPath(), mergedContent);

                    return LuaValue.TRUE;
                } catch (IOException e) {
                    e.printStackTrace();
                    return LuaValue.FALSE;
                }
            }
        });

        globals.set("playSound", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue sound) {
                GameFactory.soundSystem.play(SoundSystem.getSound(String.valueOf(sound)));
                return LuaValue.valueOf(String.valueOf(sound));
            }
        });

        globals.set("changeWindowTitle", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue title) {
                GameEngine.windowManager.setWindowTitle(title.tojstring());
                return LuaValue.valueOf(String.valueOf(title));
            }
        });

        globals.set("getLangFromKey", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue key) {
                return LuaValue.valueOf(GameFactory.langHelper.get(key.tojstring()));
            }
        });


        // Add more functions as needed
    }

    /**
     * Determines if the given file name indicates that it is an "init" script.
     *
     * @param fileName the name of the Lua file
     * @return true if the file is an "init" script, false otherwise
     */
    public static boolean isInitFile(String fileName) {
        return fileName.startsWith("init_");
    }

    /**
     * Determines if the given file name indicates that it is an "postinit" script.
     *
     * @param fileName the name of the Lua file
     * @return true if the file is an "postinit" script, false otherwise
     */
    public static boolean isPostInitFile(String fileName) {
        return fileName.startsWith("postinit_");
    }

    /**
     * Determines if the given file name indicates that it is an "imgui" script.
     *
     * @param fileName the name of the Lua file
     * @return true if the file is an "imgui" script, false otherwise
     */
    public static boolean isImguiFile(String fileName) {
        return fileName.startsWith("imgui_"); // Example: imgui_MyCustomUI.lua
    }

    /**
     * Determines if the given file name indicates that it is a "tick" script.
     *
     * @param fileName the name of the Lua file
     * @return true if the file is a "tick" script, false otherwise
     */
    public static boolean isTickFile(String fileName) {
        return fileName.startsWith("tick_");
    }

    /**
     * Determines if the given file name indicates that it is a "tick" script.
     *
     * @param fileName the name of the Lua file
     * @return true if the file is a "autorun" script, false otherwise
     */
    public static boolean isMapAutorunFile(String fileName) {
        return fileName.startsWith("autorun_");
    }


    public static void categorizeScripts(String directoryPath) {
        File directory = new File(FileUtils.getCurrentWorkingDirectory(directoryPath));
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".lua"));
        if (files != null) {
            for (File file : files) {
                if (isInitFile(file.getName())) {
                    initScripts.add(file.getName());
                }else if (isPostInitFile(file.getName())) {
                    postInitScripts.add(file.getName());
                } else if (isTickFile(file.getName())) {
                    tickScripts.add(file.getName());
                } else if (isImguiFile(file.getName())) {
                    imguiScripts.add(file.getName());
                }else if (isMapAutorunFile(file.getName())) {
                    mapAutorunScripts.add(file.getName());
                }
            }
        }
    }

    /**
     * Executes all init scripts.
     */
    public static void executeInitScripts() {
        for (String script : initScripts) {
            RainLogger.LUA_LOGGER.info("Executing init script: " + script);
            loadScript(script, "resources/scripts/");
        }
    }

    /**
     * Executes all init scripts.
     */
    public static void executePostInitScripts() {
        for (String script : postInitScripts) {
            RainLogger.LUA_LOGGER.info("Executing postinit script: " + script);
            loadScript(script, "resources/scripts/");
        }
    }

    /**
     * Executes all tick scripts.
     */
    public static void executeTickScripts() {
        for (String script : tickScripts) {
            loadScript(script, "resources/scripts/");
        }
    }

    /**
     * Executes a map script
     */
    public static void executeMapScript(String mapName) {
        for (String script : mapAutorunScripts) {
            if(script.endsWith(mapName + ".lua")){
                RainLogger.LUA_LOGGER.info("Loading: " + script);
                loadScript(script, "resources/scripts/");
            }

        }
    }
    
    /**
     * Executes all Lua scripts.
     */
    public static void executeAllImguiScripts() {
        for (String script : imguiScripts) {
            loadScript(script, "resources/scripts/");
        }
    }

    /**
     * Loads and executes a Lua script from the specified path.
     *
     * @param scriptPath the relative path to the Lua script file within the "resources/scripts/" directory
     */
    public static void loadScript(String scriptPath) {
        loadScript(scriptPath,"resources/scripts/");
    }

    /**
     * Loads and executes a Lua script from the specified path.
     *
     * @param scriptPath the path to the Lua script file
     * @param relativePath the relative path to the script  Ex: "resources/scripts/"
     */
    public static void loadScript(String scriptPath, String relativePath) {
        try {
            Globals globals = luaEngine.getGlobals();
            String script = FileUtils.readFile(FileUtils.getCurrentWorkingDirectory(relativePath + scriptPath));  // Read the script content
            LuaValue chunk = globals.load(script, scriptPath);  // Load the script from content
            chunk.call();  // Execute the script
        } catch (FileNotFoundException e) {
            RainLogger.RAIN_LOGGER.error("Error loading Script! FileNotFound");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
