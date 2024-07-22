package com.toxicrain.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private FileUtils() {}

    @Deprecated
    public static InputStream getFileFromResourceAsStream(String filePath) {
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Error while accessing resource, file not found! " + filePath);
        } else {
            return inputStream;
        }
    }

    /**
     * Utility method to read the file content into a string
     *
     * @param filePath the path to the file
     * @return the content of the file as a string
     * @throws IOException if an I/O error occurs
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine);
            }
        }
        return contentBuilder.toString();
    }

    /**
     * Utility method to get the size of a file as a long.
     *
     * @param filePath the path to the file
     * @return the size of the file (in bytes) as a long
     */
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get file size: " + filePath, e);
        }
    }
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * Converts a relative path to an absolute path based on the current working directory.
     *
     * @param relativePath the relative path to convert
     * @return the absolute path as a String
     */
    public static String getCurrentWorkingDirectory(String relativePath) {
        String fullPath = Paths.get(relativePath).toAbsolutePath().toString();
        return fullPath;
    }

    /**
     * Converts a relative path to an absolute path based on the current working directory.
     * Checks if the first path exists; if not, it uses the second path.
     *
     * @param primaryPath the primary path to check and convert
     * @param fallbackPath the fallback path to use if the primary path doesn't exist
     * @return the absolute path as a String
     */
    public static String getCurrentWorkingDirectory(String primaryPath, String fallbackPath) {
        Path path = Paths.get(primaryPath);
        if (Files.exists(path)) {
            return path.toAbsolutePath().toString();
        } else {
            Path fallback = Paths.get(fallbackPath);
            return fallback.toAbsolutePath().toString();
        }
    }

}
