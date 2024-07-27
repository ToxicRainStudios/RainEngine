package com.toxicrain.gui;

import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.util.TextEngine;



public class Menu {

    private static TextEngine textEngine;
    private static TextEngine textPart1;
    private static TextEngine textPart2;
    private static TextEngine textPart3;

   public static void render(BatchRenderer batchRenderer){

        textEngine.render(batchRenderer);
        textPart1.render(batchRenderer);
        textPart2.render(batchRenderer);
        textPart3.render(batchRenderer);


    }
    public static void initalizeMenu(){

        textEngine = new TextEngine("sigma tropism", 0, -23);
        textPart1 = new TextEngine("sigma", 10, -23);
        textPart2 = new TextEngine("theta", -10, -23);
        textPart3 = new TextEngine("the sigma begins", 0, -18);


    }
    public static void updateMenu(){




    }



}
