package com.toxicrain.rainengine.gui.editor;

import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.TileInfo;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.PaletteInfoParser;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.FileUtils;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImMapEditorMenu extends BaseInstanceable<ImMapEditorMenu> {

    private final ImString mapNameField = new ImString(256);
    private final ImInt widthField = new ImInt(10);
    private final ImInt heightField = new ImInt(10);
    private final ImInt spawnXField = new ImInt(0);
    private final ImInt spawnYField = new ImInt(0);

    private final List<ImFloat[]> lights = new ArrayList<>();
    private final List<String> slices = new ArrayList<>();
    private final List<String> submaps = new ArrayList<>();

    private final int padding = 20;
    private String currentMapName;
    private boolean openLoadMapPopup = false;
    private final ImString loadMapNameInput = new ImString(256);

    // Tile palette & Lua editor
    private int selectedTileIndex = -1;
    private final List<Character> tilePalette = new ArrayList<>();
    private final ImString luaScriptContent = new ImString(8192);

    public static ImMapEditorMenu getInstance() {
        return BaseInstanceable.getInstance(ImMapEditorMenu.class);
    }

    public void draw() {
        // --- Fullscreen dockspace ---
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("MainDockSpaceWindow", windowFlags);
        ImGui.popStyleVar(2);

        int dockspaceID = ImGui.getID("MapEditorDockSpace");
        ImGui.dockSpace(dockspaceID, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

        // --- Menu Bar ---
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Load Map")) {
                    loadMapNameInput.set("");
                    openLoadMapPopup = true;
                }
                if (ImGui.menuItem("Save Map")) onSaveClicked();
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
        ImGui.end(); // End dockspace

        // --- Load Map Popup ---
        if (openLoadMapPopup) {
            ImGui.openPopup("Load Map");
            openLoadMapPopup = false;
        }
        if (ImGui.beginPopupModal("Load Map", ImGuiWindowFlags.AlwaysAutoResize)) {
            ImGui.text("Enter the map name to load:");
            ImGui.inputText("Map Name", loadMapNameInput);
            if (ImGui.button("Load")) {
                if (!loadMapNameInput.get().trim().isEmpty()) {
                    mapNameField.set(loadMapNameInput.get().trim());
                    onLoadMap();
                    ImGui.closeCurrentPopup();
                }
            }
            ImGui.sameLine();
            if (ImGui.button("Cancel")) ImGui.closeCurrentPopup();
            ImGui.endPopup();
        }

        // --- Settings Panel ---
        ImGui.begin("Settings", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("Map Info");
        ImGui.spacing();
        ImGui.pushItemWidth(-1);
        ImGui.beginDisabled();
        ImGui.inputText("Map Name", mapNameField);
        ImGui.endDisabled();
        ImGui.popItemWidth();
        ImGui.pushItemWidth(100);
        ImGui.inputInt("Width", widthField);
        ImGui.sameLine();
        ImGui.inputInt("Height", heightField);
        ImGui.popItemWidth();
        ImGui.pushItemWidth(100);
        ImGui.inputInt("Spawn X", spawnXField);
        ImGui.sameLine();
        ImGui.inputInt("Spawn Y", spawnYField);
        ImGui.popItemWidth();
        ImGui.end();

        // --- Properties Panel ---
        ImGui.begin("Properties", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("Lights");
        if (ImGui.button("Add Light")) lights.add(new ImFloat[]{new ImFloat(0), new ImFloat(0), new ImFloat(1)});
        ImGui.spacing();
        ImGui.beginChild("LightsList", 0, 150, true);
        for (int i = 0; i < lights.size(); i++) {
            ImFloat[] light = lights.get(i);
            ImGui.pushID(i);
            ImGui.text("X:"); ImGui.sameLine();
            ImGui.pushItemWidth(50); ImGui.inputFloat("##x" + i, light[0]); ImGui.popItemWidth();
            ImGui.sameLine(); ImGui.text("Y:"); ImGui.sameLine();
            ImGui.pushItemWidth(50); ImGui.inputFloat("##y" + i, light[1]); ImGui.popItemWidth();
            ImGui.sameLine(); ImGui.text("Strength:"); ImGui.sameLine();
            ImGui.pushItemWidth(50); ImGui.inputFloat("##strength" + i, light[2]); ImGui.popItemWidth();
            ImGui.sameLine();
            if (ImGui.button("X")) lights.remove(i--);
            ImGui.popID();
        }
        ImGui.endChild();
        ImGui.separator();
        ImGui.text("Slices");
        ImGui.beginChild("SlicesList", 0, 100, true);
        for (String slice : slices) ImGui.textWrapped(slice);
        ImGui.endChild();
        ImGui.separator();
        ImGui.text("Submaps");
        ImGui.beginChild("SubmapsList", 0, 100, true);
        for (String submap : submaps) ImGui.textWrapped(submap);
        ImGui.endChild();
        ImGui.end();

        // --- Tile Palette Panel ---
        ImGui.begin("Tile Palette", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("Select Tile:");
        int tilesPerRow = 8;

        for (int i = 0; i < tilePalette.size(); i++) {
            ImGui.pushID(i);  // <-- Give each button a unique ID

            char tileChar = tilePalette.get(i);
            TileInfo tileInfo = PaletteInfoParser.getInstance().getTileInfo(tileChar);
            TextureRegion region = TextureSystem.getInstance().getRegion(tileInfo.getTextureResource());

            if (ImGui.imageButton(
                    TextureSystem.getInstance().getAtlasTextureId(),
                    32, 32,
                    region.getU0(), region.getV0(),
                    region.getU1(), region.getV1(),
                    selectedTileIndex == i ? 0x88888888 : 0x00000000
            )) {
                selectedTileIndex = i;
            }

            ImGui.popID();  // <-- Restore ID stack

            if ((i + 1) % tilesPerRow != 0)
                ImGui.sameLine();
        }

        ImGui.end();

        // --- Lua Script Editor ---
        ImGui.begin("Lua Script Editor");
        ImGui.inputTextMultiline("##lua", luaScriptContent, -1, 300);
        if (ImGui.button("Save Lua")) {
            if (currentMapName != null && !currentMapName.isEmpty()) {
                String scriptFilePath = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.MAP_PATH + currentMapName + ".lua");
                try { FileUtils.writeFile(scriptFilePath, luaScriptContent.get()); }
                catch (IOException e) { throw new RuntimeException(e); }
            }
        }
        ImGui.end();

        // --- Map Canvas ---
        ImGui.begin("Map");
        float canvasWidth = ImGui.getContentRegionAvailX();
        float canvasHeight = ImGui.getContentRegionAvailY();
        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        ImDrawList drawList = ImGui.getWindowDrawList();

        int gridW = Math.max(widthField.get(), 1);
        int gridH = Math.max(heightField.get(), 1);
        float cellSize = Math.min((canvasWidth - 2 * padding) / gridW, (canvasHeight - 2 * padding) / gridH);
        float originX = canvasPos.x + padding;
        float originY = canvasPos.y + padding;

        drawList.addRectFilled(canvasPos.x, canvasPos.y, canvasPos.x + canvasWidth, canvasPos.y + canvasHeight, 0xFF1E1E1E);
        drawList.addRect(canvasPos.x, canvasPos.y, canvasPos.x + canvasWidth, canvasPos.y + canvasHeight, 0xFF505050);

        for (int x = 0; x <= gridW; x++) drawList.addLine(originX + x * cellSize, originY, originX + x * cellSize, originY + gridH * cellSize, 0xFF404040);
        for (int y = 0; y <= gridH; y++) drawList.addLine(originX, originY + y * cellSize, originX + gridW * cellSize, originY + y * cellSize, 0xFF404040);

        // Draw tiles with texture atlas
        for (int y = 0; y < slices.size(); y++) {
            String row = slices.get(y);
            for (int x = 0; x < row.length(); x++) {
                char tileChar = row.charAt(x);
                if (tileChar != ' ') {
                    TileInfo tileInfo = PaletteInfoParser.getInstance().getTileInfo(tileChar);
                    TextureRegion region = TextureSystem.getInstance().getRegion(tileInfo.getTextureResource());

                    float x0 = originX + x * cellSize;
                    float y0 = originY + y * cellSize;
                    float x1 = x0 + cellSize;
                    float y1 = y0 + cellSize;

                    drawList.addImage(
                            TextureSystem.getInstance().getAtlasTextureId(),
                            x0, y0, x1, y1,
                            region.getU0(), region.getV0(), region.getU1(), region.getV1()
                    );
                }
            }
        }

        // Mouse tile editing
        Vector2 mousePos = new Vector2(ImGui.getMousePosX(), ImGui.getMousePosY());
        boolean isHoveringCanvas = mousePos.x > originX && mousePos.x < originX + gridW * cellSize &&
                mousePos.y > originY && mousePos.y < originY + gridH * cellSize;

        if (isHoveringCanvas && selectedTileIndex >= 0) {
            int tileX = (int) ((mousePos.x - originX) / cellSize);
            int tileY = (int) ((mousePos.y - originY) / cellSize);

            if (ImGui.isMouseClicked(0)) updateSlice(tileX, tileY, tilePalette.get(selectedTileIndex));
            if (ImGui.isMouseClicked(1)) updateSlice(tileX, tileY, ' ');
        }

        ImGui.dummy(canvasWidth, canvasHeight);
        ImGui.end();
    }

    private void updateSlice(int x, int y, char tileChar) {
        if (y >= slices.size()) return;
        StringBuilder row = new StringBuilder(slices.get(y));
        while (row.length() <= x) row.append(' ');
        row.setCharAt(x, tileChar);
        slices.set(y, row.toString());
    }

    private void onLoadMap() {
        currentMapName = mapNameField.get();
        try {
            LightSystem.getLightSources().clear();
            MapInfoParser.getInstance().parseMapFile(currentMapName);

            Vector2 size = MapInfoParser.getInstance().mapSize;
            Vector2 spawn = MapInfoParser.getInstance().playerSpawnPos;

            widthField.set((int) size.x);
            heightField.set((int) size.y);
            spawnXField.set((int) spawn.x);
            spawnYField.set((int) spawn.y);

            JSONArray topArray = MapInfoParser.getInstance().getMapJson();
            JSONObject mainMap = topArray.getJSONObject(0);

            JSONArray sliceArray = mainMap.getJSONArray("slices").getJSONArray(0);
            slices.clear();
            for (int i = 0; i < sliceArray.length(); i++) slices.add(sliceArray.getString(i));

            submaps.clear();
            JSONArray subMaps = mainMap.optJSONArray("subMaps");
            if (subMaps != null) {
                for (int i = 0; i < subMaps.length(); i++) {
                    JSONObject sm = subMaps.getJSONObject(i);
                    submaps.add(sm.getString("name") + " (" + sm.getInt("offsetX") + "," + sm.getInt("offsetY") + ")");
                }
            }

            lights.clear();
            for (float[] light : LightSystem.getLightSources()) {
                lights.add(new ImFloat[]{new ImFloat(light[0]), new ImFloat(light[1]), new ImFloat(light[2])});
            }

            // Load Lua script
            String scriptFilePath = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.BASE_PATH + "/scripts/" + "autorun_" + currentMapName + ".lua");
            try { luaScriptContent.set(FileUtils.readFile(scriptFilePath)); } catch (Exception e) { luaScriptContent.set("-- Lua script not found"); }

            // Load tile palette
            tilePalette.clear();
            for (char c : PaletteInfoParser.getInstance().getCollisionTiles()) tilePalette.add(c);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSaveClicked() {
        if (currentMapName == null) return;
        try {
            String file = FileUtils.getCurrentWorkingDirectory(Constants.FileConstants.MAP_PATH + currentMapName + ".json");
            String jsonStr = FileUtils.readFile(file);
            JSONArray jsonArray = new JSONArray(jsonStr);

            JSONObject mainPart = jsonArray.getJSONObject(0);
            mainPart.put("xsize", widthField.get());
            mainPart.put("ysize", heightField.get());
            mainPart.put("playerx", spawnXField.get());
            mainPart.put("playery", spawnYField.get());

            JSONArray lightingArray = new JSONArray();
            LightSystem.getLightSources().clear();
            for (ImFloat[] light : lights) {
                JSONObject obj = new JSONObject();
                obj.put("x", light[0].get());
                obj.put("y", light[1].get());
                obj.put("strength", light[2].get());
                lightingArray.put(obj);

                LightSystem.addLightSource(light[0].get(), light[1].get(), light[2].get());
            }
            mainPart.put("lighting", lightingArray);

            JSONArray sliceArray = new JSONArray();
            JSONArray innerArray = new JSONArray();
            for (String row : slices) innerArray.put(row);
            sliceArray.put(innerArray);
            mainPart.put("slices", sliceArray);

            FileUtils.writeFile(file, jsonArray.toString(4));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
