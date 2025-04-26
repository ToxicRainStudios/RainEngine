package com.toxicrain.rainengine.core.json;

import com.toxicrain.rainengine.core.logging.RainLogger;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.toxicrain.rainengine.util.FileUtils;

/**
 * SettingsInfoParser dynamically loads and manages settings.json file
 */
public class SettingsInfoParser {
    private static SettingsInfoParser instance = null;
    private static final String SETTINGS_PATH = "resources/json/settings.json";
    private JSONObject settingsJson;
    private final Map<String, Object> settings = new HashMap<>();

    private SettingsInfoParser() {
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

            // Dynamically load settings
            for (String key : settingsJson.keySet()) {
                settings.put(key, settingsJson.get(key));
            }
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Failed to load settings file: {}", filePath);
            e.printStackTrace();
            settingsJson = new JSONObject(); // Prevent null issues
        }
    }

    public void modifySetting(String key, Object newValue) {
        if (settingsJson == null) {
            RainLogger.RAIN_LOGGER.error("Settings JSON not initialized");
            return;
        }

        settings.put(key, newValue);
        settingsJson.put(key, newValue);
        saveSettings();
    }

    private void saveSettings() {
        String filePath = FileUtils.getCurrentWorkingDirectory(SETTINGS_PATH);
        try {
            Files.write(Paths.get(filePath), settingsJson.toString(4).getBytes());
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Failed to save settings file: {}", filePath);
            e.printStackTrace();
        }
    }

    // Generic getter methods
    public boolean getBoolean(String key, boolean defaultValue) {
        return settings.getOrDefault(key, defaultValue) instanceof Boolean ? (Boolean) settings.get(key) : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        return settings.getOrDefault(key, defaultValue) instanceof Number ? ((Number) settings.get(key)).intValue() : defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        return settings.getOrDefault(key, defaultValue) instanceof Number ? ((Number) settings.get(key)).floatValue() : defaultValue;
    }

    public String getString(String key, String defaultValue) {
        return settings.getOrDefault(key, defaultValue) instanceof String ? (String) settings.get(key) : defaultValue;
    }

    public boolean getVsync(){
        return getBoolean("vSync", true);
    }
    public float getWindowHeight(){
        return getFloat("windowHeight", 1080);
    }
    public float getWindowWidth(){
        return getFloat("windowWidth", 1920);
    }
    public float getFOV(){
        return getFloat("fov", 90);
    }
    public String getLanguage(){
        return getString("language", "fr");
    }
}
