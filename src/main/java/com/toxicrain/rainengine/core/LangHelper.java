package com.toxicrain.rainengine.core;

import com.toxicrain.rainengine.core.logging.RainLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.Properties;

public class LangHelper {

    private final String baseName;
    private final Path directory;
    private Locale currentLocale;
    private Properties properties;
    private WatchService watchService;
    private Thread watchThread;

    /**
     * Constructs a LangHelper with external file support, locale selection, and auto-reload.
     *
     * @param baseName  The base name of the properties files (e.g., "lang").
     * @param directory The directory containing the properties files.
     * @param locale    The starting locale.
     */
    public LangHelper(String baseName, Path directory, Locale locale) {
        this.baseName = baseName;
        this.directory = directory;
        this.currentLocale = locale;
        loadProperties();
        startWatchService();
    }

    /**
     * Loads the properties file based on the current locale.
     */
    private void loadProperties() {
        properties = new Properties();
        Path filePath = getLocalizedFilePath();

        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
            properties.load(fis);
            RainLogger.RAIN_LOGGER.info("Loaded properties from {}", filePath);
        } catch (IOException e) {
            RainLogger.RAIN_LOGGER.error("Failed to load properties file: {}", filePath);
            e.printStackTrace();
        }
    }

    /**
     * Builds the file path for the current locale.
     */
    private Path getLocalizedFilePath() {
        String localeSuffix = currentLocale.toString(); // e.g., "en_US" or "en"
        Path localizedFile = directory.resolve(baseName + "_" + localeSuffix + ".properties");
        if (Files.exists(localizedFile)) {
            return localizedFile;
        }

        // Fallback: try just the language (e.g., "en")
        Path languageFile = directory.resolve(baseName + "_" + currentLocale.getLanguage() + ".properties");
        if (Files.exists(languageFile)) {
            return languageFile;
        }

        // Fallback: base file (no locale)
        return directory.resolve(baseName + ".properties");
    }

    /**
     * Gets a property value by key.
     */
    public String get(String key) {
        return properties.getProperty(key, "Missing key: " + key);
    }

    /**
     * Changes the locale and reloads the properties file.
     */
    public void changeLocale(Locale locale) {
        this.currentLocale = locale;
        loadProperties();
    }

    /**
     * Starts the watch service to auto-reload properties on file changes.
     */
    private void startWatchService() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            watchThread = new Thread(this::watchLoop);
            watchThread.setDaemon(true);
            watchThread.start();
        } catch (IOException e) {
            System.err.println("Failed to start WatchService: " + e.getMessage());
        }
    }

    /**
     * The file watch loop that reloads the file if changed.
     */
    private void watchLoop() {
        while (true) {
            try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    Path changed = (Path) event.context();
                    Path currentFile = getLocalizedFilePath().getFileName();

                    if (changed.equals(currentFile)) {
                        RainLogger.RAIN_LOGGER.debug("Detected change in {}, reloading...", changed);
                        loadProperties();
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Stops the WatchService (optional if you want to clean up threads on shutdown).
     */
    public void stopWatching() {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
