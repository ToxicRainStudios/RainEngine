package com.toxicrain.gui;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.ButtonUtils;
import com.toxicrain.util.CollisionUtils;
import com.toxicrain.util.MouseUtils;
import com.toxicrain.util.TextEngine;

import static com.toxicrain.core.GameEngine.window;


public class Menu {

    public static TextEngine textEngine;
    public static TextEngine textPart1;
    public static TextEngine textPart2;
    public static TextEngine textPart3;
    public static ButtonUtils startButton;


    public static void render(BatchRenderer batchRenderer,long window){

        textEngine.render(batchRenderer);
        textPart1.render(batchRenderer);
        textPart2.render(batchRenderer);
        textPart3.render(batchRenderer);
        ButtonUtils.update(startButton,window);
        ButtonUtils.render(startButton,batchRenderer);


    }

    public static void initalizeMenu(){
        startButton = new ButtonUtils(2, 2, 2, 1);
        textEngine = new TextEngine("sigma tropism", 0, -23);
        textPart1 = new TextEngine("sigma", 10, -23);
        textPart2 = new TextEngine("theta", -10, -23);
        textPart3 = new TextEngine("the sigma begins", 0, -18);


    }
    public static void updateMenu(){




    }



}
