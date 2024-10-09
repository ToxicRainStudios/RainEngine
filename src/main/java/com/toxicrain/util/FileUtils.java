package com.toxicrain.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * Returns the {@link System#getProperty} user.home
     *
     * @return System.getProperty("user.home")
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * Returns the {@link System#getProperty} user.dir
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");

    }

    private static final Map<String, String> cachedAbsolutePaths = new ConcurrentHashMap<>();

    /**
     * Converts a relative path to an absolute path based on the current working directory.
     * If the path has been converted before, it returns the cached value.
     *
     * @param relativePath the relative path to convert
     * @return the absolute path as a String
     */
    public static String getCurrentWorkingDirectory(String relativePath) {
        // Check if the absolute path for this relative path is already cached
        return cachedAbsolutePaths.computeIfAbsent(relativePath, key ->
                Paths.get(key).toAbsolutePath().toString()
        );
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
            return getCurrentWorkingDirectory(primaryPath);
        } else {
            return getCurrentWorkingDirectory(fallbackPath);
        }
    }

    public static void writeFile(String filePath, String data) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(data);
        }
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
        Path path = Paths.get(resource);
        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("File not readable: " + resource);
        }
        ByteBuffer buffer;
        try (SeekableByteChannel fc = Files.newByteChannel(path)) {
            buffer = ByteBuffer.allocateDirect((int) fc.size() + 1);
            while (fc.read(buffer) != -1);
        }
        buffer.flip();
        return buffer;
    }

}
