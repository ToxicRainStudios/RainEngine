package com.toxicrain.core;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class Network {

    public static void downloadFile(String fileURL, Path destination) {
        try {
            URI uri = URI.create(fileURL);
            // Open a stream from the URL and copy its contents to the destination path
            try (InputStream inputStream = uri.toURL().openStream()) {
                Files.copy(inputStream, destination);
                System.out.println("File downloaded to: " + destination.toString());
            }
        } catch (Exception e) {
            Logger.printERROR("Failed to download the file from: " + fileURL);
            Logger.printERROR("Error: " + e.getMessage());
        }
    }

}
