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
    }public static float playerX = 15.0f; // Camera X position
    public static float playerY = 15.0f; // Camera Y position
    public static float playerZ = 5.0f; // Camera Z position
    public static float cameraSpeed = 0.02f; // Camera Speed
    public static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    public static float scrollOffset = 0.0f; // Track the scroll input

    private static void handleCollisions() {

        for (int i = MapInfoParser.extentTop.size()-1; i >= 0; i--) {

            if((playerY <= MapInfoParser.extentTop.get(i)) && (playerY >= MapInfoParser.extentCenterY.get(i))) {
                if ((playerX >= MapInfoParser.extentLeft.get(i)) && !(playerX >= MapInfoParser.extentCenterX.get(i))) {
                    playerY += 0.02f;
                } else if ((playerX <= MapInfoParser.extentRight.get(i)) &&!(playerX <= MapInfoParser.extentCenterX.get(i))) {
                    playerY += 0.02f;
                }
            }
             if((playerY >= MapInfoParser.extentBottom.get(i)) && (playerY <= MapInfoParser.extentCenterY.get(i))) {
               if ((playerX >= MapInfoParser.extentLeft.get(i)) && !(playerX >= MapInfoParser.extentCenterX.get(i))) {
                    playerY -= 0.02f;
               } else if ((playerX <= MapInfoParser.extentRight.get(i)) && !(playerX <= MapInfoParser.extentCenterX.get(i))) {
                   playerY -= 0.02f;
               }
            }
           if((playerX <= MapInfoParser.extentRight.get(i)) && (playerX >= MapInfoParser.extentCenterX.get(i))) {
                if ((playerY >= MapInfoParser.extentBottom.get(i)) && !(playerY > MapInfoParser.extentCenterY.get(i))) {
                    playerX += 0.02f;
                } else if ((playerY <= MapInfoParser.extentTop.get(i)) && !(playerY <= MapInfoParser.extentCenterY.get(i))) {
                    playerX += 0.02f;
                }
            }
             if((playerX >= MapInfoParser.extentLeft.get(i)) && (playerX <= MapInfoParser.extentCenterX.get(i))) {
                if ((playerY >= MapInfoParser.extentBottom.get(i)) && !(playerY >= MapInfoParser.extentCenterY.get(i))) {
                    playerX -= 0.02f;
                } else if ((playerY <= MapInfoParser.extentTop.get(i)) && !(playerY <= MapInfoParser.extentCenterY.get(i))) {
                    playerX -= 0.02f;
                }
            }







            }
        }


    private static void handleMovement(float opX, float opY){

            playerX = playerX + ((cameraSpeed/ 2)*opX);
            playerY = playerY + ((cameraSpeed/ 2)*opY);
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


        // Update playerZ based on the scroll input
        playerZ += scrollOffset * scrollSpeed;

        // Cap playerZ at max 25 and min 3
        if (playerZ > GameInfoParser.maxZoom) {
            playerZ = GameInfoParser.maxZoom;

        }
        if (playerZ < 3) {
            playerZ = 3;
        }

        scrollOffset = 0.0f; // Reset the scroll offset after applying it
    }
}