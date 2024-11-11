package com.toxicrain.core;

import lombok.NonNull;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {

    public static void downloadFile(@NonNull String fileURL, @NonNull Path destination) {
        try {
            URI uri = URI.create(fileURL);
            // Open a stream from the URL and copy its contents to the destination path
            try (InputStream inputStream = uri.toURL().openStream()) {
                Files.copy(inputStream, destination);
                Logger.printLOG("File downloaded to: " + destination);
            }
        } catch (Exception e) {
            Logger.printERROR("Failed to download the file from: " + fileURL);
            Logger.printERROR("Error: " + e.getMessage());
        }
    }

}
