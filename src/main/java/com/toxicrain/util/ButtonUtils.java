package com.toxicrain.util;

import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import org.lwjgl.glfw.GLFW;

import static com.toxicrain.core.GameEngine.window;
import static com.toxicrain.util.MouseUtils.convertToOpenGLCoordinates;
import static org.lwjgl.glfw.GLFW.*;

public class ButtonUtils {
    private static CollisionUtils menucol;
    public static MouseUtils mouse = new MouseUtils(window);
    public float positionX = 0;
    public float positionY = 0;
    public float sizeX = 0;
    public float sizeY = 0;
    public float[] color =Color.toFloatArray(Color.WHITE);
    ;


    public ButtonUtils(float X, float Y, float xSize, float ySize){
        positionX = X;
        positionY = Y;
        sizeX = xSize;
        sizeY = ySize;
    }

    public static void render(ButtonUtils button, BatchRenderer batchRenderer){
        batchRenderer.addTexture(TextureUtils.playButton, button.positionX, button.positionY, 1.01f,0, button.sizeX, button.sizeY,button.color);


    }


    public static void update(ButtonUtils button,long window) {
        if (menucol == null) {
            menucol = new CollisionUtils();
        }

        float[] cord= convertToOpenGLCoordinates(mouse.getMousePosition()[0], mouse.getMousePosition()[1], (int)SettingsInfoParser.windowWidth,(int)SettingsInfoParser.windowHeight);
        menucol.handleCollisions(0.001f, menucol, cord[0],cord[1] , button.sizeX,button.sizeY,button.positionX,button.positionY,'Q');
        if(menucol.isColliding){
            button.color = Color.toFloatArray(Color.LIGHT_BLUE);

            }

}}




