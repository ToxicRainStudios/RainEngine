package com.toxicrain.core;

import lombok.NonNull;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {

    public static void downloadFile(@NonNull String fileURL, @NonNull Path destination) {
        if (Files.exists(destination)) {
            RainLogger.printLOG("File already exists at: " + destination);
            return; // Skip the download if the file exists
        }

        try {
            URI uri = URI.create(fileURL);
            // Open a stream from the URL and copy its contents to the destination path
            try (InputStream inputStream = uri.toURL().openStream()) {
                Files.copy(inputStream, destination);
                RainLogger.printLOG("File downloaded to: " + destination);
            }
        } catch (Exception e) {
            RainLogger.printERROR("Failed to download the file from: " + fileURL);
            RainLogger.printERROR("Error: " + e.getMessage());
        }
    }

}
