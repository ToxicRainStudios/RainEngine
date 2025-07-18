package com.toxicrain.rainengine.core.editor;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.light.LightSystem;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImMapEditorMenu {

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

    public void draw() {
        // Fullscreen dockspace window
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getIO().getDisplaySizeX(), ImGui.getIO().getDisplaySizeY());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
        windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("MainDockSpaceWindow", windowFlags);
        ImGui.popStyleVar(2);

        // Create the DockSpace where windows can dock
        int dockspaceID = ImGui.getID("MapEditorDockSpace");
        ImGui.dockSpace(dockspaceID, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);

        // Menu Bar
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

        ImGui.end(); // End dockspace window

        // Load Map Modal Popup
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
            if (ImGui.button("Cancel")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }

        // Left Panel - Settings
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

        // Right Panel - Properties
        ImGui.begin("Properties", ImGuiWindowFlags.AlwaysAutoResize);

        ImGui.text("Lights");
        if (ImGui.button("Add Light")) {
            lights.add(new ImFloat[]{new ImFloat(0), new ImFloat(0), new ImFloat(1)});
        }
        ImGui.spacing();

        ImGui.beginChild("LightsList", 0, 150, true);
        for (int i = 0; i < lights.size(); i++) {
            ImFloat[] light = lights.get(i);
            ImGui.pushID(i);

            ImGui.text("X:");
            ImGui.sameLine();
            ImGui.pushItemWidth(50);
            ImGui.inputFloat("##x" + i, light[0]);
            ImGui.popItemWidth();

            ImGui.sameLine();
            ImGui.text("Y:");
            ImGui.sameLine();
            ImGui.pushItemWidth(50);
            ImGui.inputFloat("##y" + i, light[1]);
            ImGui.popItemWidth();

            ImGui.sameLine();
            ImGui.text("Strength:");
            ImGui.sameLine();
            ImGui.pushItemWidth(50);
            ImGui.inputFloat("##strength" + i, light[2]);
            ImGui.popItemWidth();

            ImGui.sameLine();

            if (ImGui.button("X")) {
                lights.remove(i--);
            }

            ImGui.popID();
        }
        ImGui.endChild();


        ImGui.separator();

        ImGui.text("Slices");
        ImGui.beginChild("SlicesList", 0, 100, true);
        for (String slice : slices) {
            ImGui.textWrapped(slice);
        }
        ImGui.endChild();

        ImGui.separator();

        ImGui.text("Submaps");
        ImGui.beginChild("SubmapsList", 0, 100, true);
        for (String submap : submaps) {
            ImGui.textWrapped(submap);
        }
        ImGui.endChild();

        ImGui.end();

        // Center Panel - Map Canvas
        ImGui.begin("Map");

        float canvasWidth = ImGui.getContentRegionAvailX();
        float canvasHeight = ImGui.getContentRegionAvailY();
        float pad = padding;

        ImVec2 canvasPos = ImGui.getCursorScreenPos();
        ImDrawList drawList = ImGui.getWindowDrawList();

        int gridW = Math.max(widthField.get(), 1);
        int gridH = Math.max(heightField.get(), 1);
        float cellSize = Math.min((canvasWidth - 2 * pad) / gridW, (canvasHeight - 2 * pad) / gridH);

        float originX = canvasPos.x + pad;
        float originY = canvasPos.y + pad;

        drawList.addRectFilled(canvasPos.x, canvasPos.y, canvasPos.x + canvasWidth, canvasPos.y + canvasHeight, 0xFF1E1E1E);
        drawList.addRect(canvasPos.x, canvasPos.y, canvasPos.x + canvasWidth, canvasPos.y + canvasHeight, 0xFF505050);

        for (int x = 0; x <= gridW; x++) {
            float px = originX + x * cellSize;
            drawList.addLine(px, originY, px, originY + gridH * cellSize, 0xFF404040);
        }
        for (int y = 0; y <= gridH; y++) {
            float py = originY + y * cellSize;
            drawList.addLine(originX, py, originX + gridW * cellSize, py, 0xFF404040);
        }

        if (false){ //TODO The players uses different cords than tiles but i cant be bothered to do the math -strubium
            float spawnX = originX + (spawnXField.get() / 32f) * cellSize;
            float spawnY = originY - (spawnYField.get() / 32f) * cellSize;

            float spawnRadius = cellSize / 2;
            drawList.addCircleFilled(spawnX, spawnY, spawnRadius, 0xAA00FF00);
        }

        for (ImFloat[] light : lights) {
            float lx = light[0].get();
            float ly = light[1].get();
            float strength = light[2].get();

            float px = originX + lx * cellSize;
            float py = originY - ly * cellSize;
            float radius = cellSize * 0.3f;
            float glowRadius = radius * 2.5f * strength;

            drawList.addCircleFilled(px, py, glowRadius, 0x66FFFF00);
            drawList.addCircleFilled(px, py, radius, 0xFFFFFF00);
            drawList.addCircle(px, py, radius, 0xFFAA5500, 0, 2.0f);
            drawList.addText(px + 4, py - 4, 0xFF000000, String.format("%.2f", strength));
        }

        ImGui.dummy(canvasWidth, canvasHeight);
        ImGui.end();
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
            for (int i = 0; i < sliceArray.length(); i++) {
                slices.add(sliceArray.getString(i));
            }

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
                lights.add(new ImFloat[]{
                        new ImFloat(light[0]),
                        new ImFloat(light[1]),
                        new ImFloat(light[2])
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSaveClicked() {
        if (currentMapName == null) return;
        try {
            String file = FileUtils.getCurrentWorkingDirectory(
                    Constants.FileConstants.MAP_PATH + currentMapName + ".json");
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
            FileUtils.writeFile(file, jsonArray.toString(4));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
