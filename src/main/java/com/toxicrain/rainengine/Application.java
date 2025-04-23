package com.toxicrain.rainengine;

import com.toxicrain.rainengine.core.RainLogger;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.GameEngine;


import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        RainLogger.RAIN_LOGGER.info("Starting with args: {}", Arrays.toString(args));

        GameInfoParser.loadGameInfo();
        GameEngine.run();
    }

}