package com.toxicrain.core;

/**
 * Utility class for logging messages to the console.
 *
 * @author strubium
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
     * Prints a log message to the console if a condition
     * is true
     *
     * @param input The message to be logged.
     * @param bool The condition to print
     */
    public static void printLOGConditional(String input, boolean bool){
        if(bool)System.out.println("[LOG]: " + input);
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
