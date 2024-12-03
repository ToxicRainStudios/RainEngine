package com.toxicrain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging messages to the console.
 *
 * @author strubium
 */
public class RainLogger { //TODO maybe replace this? Separate loggers for Lua would be nice

    private static final Logger logger = LoggerFactory.getLogger("RainEngine");


    /**
     * Prints a log message to the console.
     *
     * @param input The message to be logged.
     */
    public static void printLOG(String input){
        logger.info(input);
    }
    /**
     * Prints a log message to the console if a condition
     * is true
     *
     * @param input The message to be logged.
     * @param bool The condition to print
     */
    public static void printLOGConditional(String input, boolean bool){
        if(bool) logger.info(input);
    }
    /**
     * Prints an error message to the console.
     *
     * @param input The error message to be logged.
     */
    public static void printERROR(String input){
        logger.error(input);
    }

}
