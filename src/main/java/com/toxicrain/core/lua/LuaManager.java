package com.toxicrain.core.lua;

import com.toxicrain.core.Logger;
import com.toxicrain.core.json.KeyInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.FileUtils;
import org.luaj.vm2.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.toxicrain.factories.GameFactory.luaEngine;

public class LuaManager {
    private final Globals globals;

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
                    Logger.printLOG("Loading Map Data");
                    MapInfoParser.parseMapFile(String.valueOf(arg));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return arg;
            }
        });

        globals.set("isKeyPressed", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(arg.toString()))) {
                    return LuaValue.TRUE;
                }
                return LuaValue.FALSE;
            }
        });

        globals.set("beginWindow", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue title) {
                GameFactory.imguiApp.luaBeginWindow(title.tojstring());
                return LuaValue.TRUE;
            }
        });

        globals.set("endWindow", new LuaFunction() {
            @Override
            public LuaValue call() {
                GameFactory.imguiApp.luaEndWindow();
                return LuaValue.TRUE;
            }
        });

        globals.set("setWindowSize", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue width, LuaValue height) {
                GameFactory.imguiApp.luaSetWindowSize(width.toint(), height.toint());
                return LuaValue.TRUE;
            }
        });

        globals.set("createLabel", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue text) {
                GameFactory.imguiApp.luaCreateLabel(text.tojstring());
                return LuaValue.TRUE;
            }
        });

        globals.set("createButton", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue label) {
                // Call the Java method and return the result as a Lua boolean
                boolean isPushed = GameFactory.imguiApp.luaCreateButton(label.tojstring());
                return LuaValue.valueOf(isPushed);
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



        // Add more functions as needed
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
            Logger.printERROR("Error loading Script! FileNotFound");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
