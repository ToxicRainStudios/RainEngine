package com.toxicrain.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging messages to the console.
 *
 * @author strubium
 */
public class RainLogger {

    public static final Logger rainLogger = LoggerFactory.getLogger("RainEngine");
    public static final Logger luaLogger = LoggerFactory.getLogger("RainLua");

    /**
     * Prints a log message to the console if a condition
     * is true
     *
     * @param input The message to be logged.
     * @param bool The condition to print
     */
    @Deprecated
    public static void printLOGConditional(String input, boolean bool){
        if(bool) rainLogger.info(input);
    }


}
