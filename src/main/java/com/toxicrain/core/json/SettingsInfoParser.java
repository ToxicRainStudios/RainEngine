package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import lombok.Getter;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import com.toxicrain.util.FileUtils;

/**
 * SettingsInfoParser parsers the settings.json file
 * needed for game functionality
 */
public class SettingsInfoParser {
    private static SettingsInfoParser instance;

    public boolean vSync = true;
    @Getter
    public float windowWidth = 1920;
    @Getter
    public float windowHeight = 1080;
    @Getter
    public float fov = 90f;

    private static final String SETTINGS_PATH = "resources/json/settings.json";
    private JSONObject settingsJson;

    // Enum to define keys for better readability
    private enum SettingKey {
        VSYNC("vSync"),
        WINDOW_WIDTH("windowWidth"),
        WINDOW_HEIGHT("windowHeight"),
        FOV("fov");

        private final String key;
        SettingKey(String key) { this.key = key; }
        @Override public String toString() { return key; }
    }

    public SettingsInfoParser() {
        loadSettings();
    }

    public static SettingsInfoParser getInstance() {
        if (instance == null) {
            instance = new SettingsInfoParser();
        }
        return instance;
    }

    private void loadSettings() {
        String filePath = FileUtils.getCurrentWorkingDirectory(SETTINGS_PATH);

        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
            settingsJson = new JSONObject(jsonString);

            // Load settings
            vSync = settingsJson.optBoolean(SettingKey.VSYNC.toString(), vSync);
            windowWidth = (float) settingsJson.optDouble(SettingKey.WINDOW_WIDTH.toString(), windowWidth);
            windowHeight = (float) settingsJson.optDouble(SettingKey.WINDOW_HEIGHT.toString(), windowHeight);
            fov = (float) settingsJson.optDouble(SettingKey.FOV.toString(), fov);

        } catch (IOException e) {
            Logger.printERROR("Failed to load settings file: " + filePath);
            e.printStackTrace();
            settingsJson = new JSONObject(); // Initialize to prevent null issues
        }
    }

    public void modifySetting(String key, Object newValue) {
        if (settingsJson == null) {
            Logger.printERROR("Settings JSON not initialized");
            return;
        }

        // Modify the setting in memory and in JSON
        settingsJson.put(key, newValue);
        saveSettings();

        // Update the value in the instance variables
        switch (key) {
            case "vSync":
                vSync = (boolean) newValue;
                break;
            case "windowWidth":
                windowWidth = ((Number) newValue).floatValue();
                break;
            case "windowHeight":
                windowHeight = ((Number) newValue).floatValue();
                break;
            case "fov":
                fov = ((Number) newValue).floatValue();
                break;
            default:
                Logger.printERROR("Unknown setting key: " + key);
        }
    }

    private void saveSettings() {
        String filePath = FileUtils.getCurrentWorkingDirectory(SETTINGS_PATH);
        try {
            Files.write(Paths.get(filePath), settingsJson.toString(4).getBytes());
        } catch (IOException e) {
            Logger.printERROR("Failed to save settings file: " + filePath);
            e.printStackTrace();
        }
    }

    public boolean getVsync(){
        return vSync;
    }


}