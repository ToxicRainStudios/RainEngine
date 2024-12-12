package com.toxicrain.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * The Crash Reporting system for RainEngine
 *
 * @author strubium
 */
public class CrashReporter implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Handle uncaught exception from any thread
        generateCrashReport(e);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void generateCrashReport(Throwable t) {
        RainLogger.printERROR("A crash occurred: " + t.getCause());

        // Log the crash to a file
        try (FileWriter fw = new FileWriter("crash_report.txt", false);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("=== Crash Report ===");
            pw.println("Thread: " + Thread.currentThread().getName());
            pw.println("Exception: " + t.getClass().getName() + ": " + t.getMessage());
            t.printStackTrace(pw); // Logs the stack trace
            pw.println("=====================");
            pw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Optionally exit the program
        System.exit(1); // Exits with error code
    }
}
