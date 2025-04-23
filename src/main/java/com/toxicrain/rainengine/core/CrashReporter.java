package com.toxicrain.rainengine.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Crash Reporting system for RainEngine
 *
 * @author strubium
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Handle uncaught exception from any thread
        generateCrashReport(e);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void generateCrashReport(Throwable t) {
        // Print error to log
        RainLogger.RAIN_LOGGER.error("A crash occurred: {}", String.valueOf(t.getCause()));

        // Generate a unique crash file name with a timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String crashFileName = "crash_report_" + timestamp + ".txt";

        try (FileWriter fw = new FileWriter(crashFileName, false);
             PrintWriter pw = new PrintWriter(fw)) {

            // Write the crash report with categorized sections
            pw.println("=== Crash Report ===");
            pw.println("Generated: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
            pw.println();

            // Category: Thread Information
            pw.println("=== Thread Information ===");
            Thread crashingThread = Thread.currentThread();
            pw.println("Thread Name: " + crashingThread.getName());
            pw.println("Thread ID: " + crashingThread.getId());
            pw.println("Thread State: " + crashingThread.getState());
            pw.println();

            // Category: Exception Details
            pw.println("=== Exception Details ===");
            pw.println("Exception Type: " + t.getClass().getName());
            pw.println("Message: " + t.getMessage());
            pw.println("Cause: " + (t.getCause() != null ? t.getCause() : "None"));
            pw.println();
            pw.println("Stack Trace:");
            t.printStackTrace(pw); // Logs the stack trace
            pw.println();

            // Category: System Information
            pw.println("=== System Information ===");
            pw.println("Operating System: " + System.getProperty("os.name"));
            pw.println("OS Version: " + System.getProperty("os.version"));
            pw.println("Java Version: " + System.getProperty("java.version"));
            pw.println("Java Vendor: " + System.getProperty("java.vendor"));
            pw.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
            pw.println("Free Memory: " + Runtime.getRuntime().freeMemory() + " bytes");
            pw.println("Total Memory: " + Runtime.getRuntime().totalMemory() + " bytes");
            pw.println();

            pw.println("=====================");
            pw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Optionally exit the program
        System.exit(1); // Exits with error code
    }
}
