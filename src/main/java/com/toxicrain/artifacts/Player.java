package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;

import static org.lwjgl.glfw.GLFW.*;



public class Player{

    public float posX;
    public float posY;
    public float posZ;
    public TextureInfo texture;

    public Player(float posX, float posY, float posZ, TextureInfo texture) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
    }public static float cameraX = 15.0f; // Camera X position
    public static float cameraY = 15.0f; // Camera Y position
    public static float cameraZ = 5.0f; // Camera Z position
    public static float cameraSpeed = 0.02f; // Camera Speed
    public static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    public static float scrollOffset = 0.0f; // Track the scroll input

    private static void handleCollisions() {

        for (int i = MapInfoParser.extentTop.size()-1; i >= 0; i--) {

            if((cameraY <= MapInfoParser.extentTop.get(i)) && (cameraY >= MapInfoParser.extentCenterY.get(i))) {
                if ((cameraX >= MapInfoParser.extentLeft.get(i)) && !(cameraX >= MapInfoParser.extentCenterX.get(i))) {
                    cameraY += 0.02f;
                } else if ((cameraX <= MapInfoParser.extentRight.get(i)) &&!(cameraX <= MapInfoParser.extentCenterX.get(i))) {
                    cameraY += 0.02f;
                }
            }
             if((cameraY >= MapInfoParser.extentBottom.get(i)) && (cameraY <= MapInfoParser.extentCenterY.get(i))) {
               if ((cameraX >= MapInfoParser.extentLeft.get(i)) && !(cameraX >= MapInfoParser.extentCenterX.get(i))) {
                    cameraY -= 0.02f;
               } else if ((cameraX <= MapInfoParser.extentRight.get(i)) && !(cameraX <= MapInfoParser.extentCenterX.get(i))) {
                   cameraY -= 0.02f;
               }
            }
           if((cameraX <= MapInfoParser.extentRight.get(i)) && (cameraX >= MapInfoParser.extentCenterX.get(i))) {
                if ((cameraY >= MapInfoParser.extentBottom.get(i)) && !(cameraY > MapInfoParser.extentCenterY.get(i))) {
                    cameraX += 0.02f;
                } else if ((cameraY <= MapInfoParser.extentTop.get(i)) && !(cameraY <= MapInfoParser.extentCenterY.get(i))) {
                    cameraX += 0.02f;
                }
            }
             if((cameraX >= MapInfoParser.extentLeft.get(i)) && (cameraX <= MapInfoParser.extentCenterX.get(i))) {
                if ((cameraY >= MapInfoParser.extentBottom.get(i)) && !(cameraY >= MapInfoParser.extentCenterY.get(i))) {
                    cameraX -= 0.02f;
                } else if ((cameraY <= MapInfoParser.extentTop.get(i)) && !(cameraY <= MapInfoParser.extentCenterY.get(i))) {
                    cameraX -= 0.02f;
                }
            }







            }
        }


    private static void handleMovement(float opX, float opY){

            cameraX = cameraX + ((cameraSpeed/ 2)*opX);
            cameraY = cameraY + ((cameraSpeed/ 2)*opY);
        }








    public static void processInput(long window) {
        //Sprinting8
        cameraSpeed = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS ? 0.1f : cameraSpeed;

        handleCollisions();

        // Handle left and right movement
        if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
        handleMovement(-1,1);
        }
        else if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            handleMovement(-1,-1);
        }
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
            handleMovement(1,1);
        }
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            handleMovement(1,-1);
        }
        else if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)handleMovement(-1,0);
        else if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)handleMovement(1,0);
        else if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) handleMovement(0,1);
        else if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) handleMovement(0,-1);


        // Update cameraZ based on the scroll input
        cameraZ += scrollOffset * scrollSpeed;

        // Cap cameraZ at max 25 and min 3
        if (cameraZ > GameInfoParser.maxZoom) {
            cameraZ = GameInfoParser.maxZoom;

        }
        if (cameraZ < 3) {
            cameraZ = 3;
        }

        scrollOffset = 0.0f; // Reset the scroll offset after applying it
    }
}