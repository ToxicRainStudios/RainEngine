package com.toxicrain.rainengine.core.logging;

import com.toxicrain.rainengine.core.json.GameInfoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging messages to the console.
 *
 * @author strubium
 */
public class RainLogger {

    public static final Logger RAIN_LOGGER = LoggerFactory.getLogger("RainEngine");
    public static final Logger LUA_LOGGER = LoggerFactory.getLogger("RainLua");

    /**The logger for this game. Use for everything related to your game*/
    public static Logger gameLogger = null;

    public static void buildLoggers(){
        gameLogger = LoggerFactory.getLogger(GameInfoParser.getInstance().gameName);
    }

    /**
     * Prints a log message to the console if a condition
     * is true
     *
     * @param input The message to be logged.
     * @param bool The condition to print
     */
    @Deprecated
    public static void printLOGConditional(String input, boolean bool){
        if(bool) RAIN_LOGGER.info(input);
    }


}
