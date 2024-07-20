package com.toxicrain.core;

/**
 * Utility class for logging messages to the console.
 */
public class Logger {

    /**
     * Prints a log message to the console.
     *
     * @param input The message to be logged.
     */
    public static void printLOG(String input){
        System.out.println("[LOG]: " + input);
    }
    /**
     * Prints an error message to the console.
     *
     * @param input The error message to be logged.
     */
    public static void printERROR(String input){
        System.err.println("[ERROR]: " + input);
    }

}
