package com.toxicrain;

import com.toxicrain.core.RainLogger;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.GameEngine;

import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        RainLogger.RAIN_LOGGER.info("Starting with args: {}", Arrays.toString(args));

        GameInfoParser.loadGameInfo();
        GameEngine.run();
    }

}