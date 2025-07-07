package com.toxicrain.rainengine;

import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.GameEngine;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        RainLogger.RAIN_LOGGER.info("Starting with args: {}", Arrays.toString(args));

        GameInfoParser.getInstance().loadGameInfo();
        GameEngine.run();
    }

    public static void restart() {
        try {
            // Get current JVM arguments
            List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();

            // Get Java binary path
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";

            // Get classpath and main class
            String classpath = System.getProperty("java.class.path");
            String mainClass = Application.class.getName();

            // Rebuild command
            List<String> command = new ArrayList<>();
            command.add(javaBin);
            command.addAll(jvmArgs);
            command.add("-cp");
            command.add(classpath);
            command.add(mainClass);

            RainLogger.RAIN_LOGGER.info("Restarting game...");

            // Start new process
            new ProcessBuilder(command).inheritIO().start();

            // Exit current process
            System.exit(0);

        } catch (Exception e) {
            RainLogger.RAIN_LOGGER.error("Failed to restart game", e);
        }
    }
}
