package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.GameInfoParser;

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
    }public static float cameraX = 0.0f; // Camera X position
    public static float cameraY = 0.0f; // Camera Y position
    public static float cameraZ = 5.0f; // Camera Z position
    public static float cameraSpeed = 0.02f; // Camera Speed
    public static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    public static float scrollOffset = 0.0f; // Track the scroll input

    public static void processInput(long window) {
        //Sprinting8
        cameraSpeed = glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS ? 0.1f : cameraSpeed;

        // Handle left and right movement
        if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
            cameraY += cameraSpeed/2;
            cameraX -= cameraSpeed/2;
        }
        else if ((glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            cameraY -= cameraSpeed/2;
            cameraX -= cameraSpeed/2;
        }
        else if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) cameraX -= cameraSpeed;
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)) {
            cameraY += cameraSpeed/2;
            cameraX += cameraSpeed/2;
        }
        else if ((glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) && (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)) {
            cameraY -= cameraSpeed/2;
            cameraX += cameraSpeed/2;
        }
        else if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) cameraX += cameraSpeed;
        else if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) cameraY += cameraSpeed;
        else if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) cameraY -= cameraSpeed;


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