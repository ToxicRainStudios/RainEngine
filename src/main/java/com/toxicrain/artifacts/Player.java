package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.render.Tile;
import com.toxicrain.util.Color;
import com.toxicrain.util.MouseUtils;
import com.toxicrain.util.WindowUtils;
import org.joml.Vector3f;

import java.util.Map;

import static com.toxicrain.core.GameEngine.window;
import static com.toxicrain.util.TextureUtils.playerTexture;
import static org.lwjgl.glfw.GLFW.*;



public class Player implements IArtifact {

    public static float posX;
    public static float posY;
    public static float posZ;
    public static float k;
    public static TextureInfo texture;
    public static boolean isSprinting;
    private static int collisionType;
    public static float cameraX = MapInfoParser.playerx; // Camera X position
    public static float cameraY = MapInfoParser.playery; // Camera Y position
    public static float cameraZ = 2; // Camera Z position
    public static float cameraSpeed = 0.02f; // Camera Speed
    public static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    public static float scrollOffset = 0.0f; // Track the scroll input
    public static Vector3f center;
    public static MouseUtils mouseInput = new MouseUtils(window);
    public Player(float posX, float posY, float posZ, TextureInfo texture, boolean isSprinting) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
        this.isSprinting = isSprinting;
    }

    private static void updatePos(float posX, float posY, float posZ) {
        Player.posX = posX;
        Player.posY = posY;
        Player.posZ = posZ;
    }

    public static void setIsSprinting(boolean sprinting) {
        Player.isSprinting = sprinting;
    }

    public static void update(){
        processInput(window);
        updatePos(cameraX, cameraY, cameraZ);
        center = WindowUtils.getCenter();
    }

    private static float[] openglMousePos;
    public static void render(BatchRenderer batchRenderer){
        // Get mouse position relative to window
        float[] mousePos = mouseInput.getMousePosition();

        // Convert mouse coordinates to OpenGL coordinates
        openglMousePos = MouseUtils.convertToOpenGLCoordinates(mousePos[0], mousePos[1], (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);

        batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], Color.toFloatArray(1.0f, Color.WHITE));
    }


    private static void handleCollisions(){
        for (int j = 1; j > -2; j -= 1) {
            k = (float) j * GameInfoParser.playerSize;
            for (int i = Tile.extentTop.size() - 1; i >= 0; i--) {

                if ((cameraY + k <= Tile.extentTop.get(i)) && (cameraY + k >= Tile.extentCenterY.get(i))) {
                    if ((cameraX + k >= Tile.extentLeft.get(i)) && !(cameraX + k >= Tile.extentCenterX.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraY += 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    } else if ((cameraX + k <= Tile.extentRight.get(i)) && !(cameraX + k <= Tile.extentCenterX.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraY += 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    }
                }
                if ((cameraY + k >= Tile.extentBottom.get(i)) && (cameraY + k <= Tile.extentCenterY.get(i))) {
                    if ((cameraX + k >= Tile.extentLeft.get(i)) && !(cameraX + k >= Tile.extentCenterX.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                               cameraY -= 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    } else if ((cameraX + k <= Tile.extentRight.get(i)) && !(cameraX + k <= Tile.extentCenterX.get(i))) {
                        for (int p = MapInfoParser.doCollide.size()-1; p >= 0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraY -= 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    }
                }
                if ((cameraX + k <= Tile.extentRight.get(i)) && (cameraX + k >= Tile.extentCenterX.get(i))) {
                    if ((cameraY + k >= Tile.extentBottom.get(i)) && !(cameraY + k > Tile.extentCenterY.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraX += 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    } else if ((cameraY + k <= Tile.extentTop.get(i)) && !(cameraY + k <= Tile.extentCenterY.get(i))) {
                        for (int p = MapInfoParser.doCollide.size()-1; p >= 0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraX += 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    }
                }
              if ((cameraX + k >= Tile.extentLeft.get(i)) && (cameraX + k <= Tile.extentCenterX.get(i))) {
                    if ((cameraY + k >= Tile.extentBottom.get(i)) && !(cameraY + k >= Tile.extentCenterY.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraX -= 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }
                        }
                    } else if ((cameraY + k <= Tile.extentTop.get(i)) && !(cameraY + k <= Tile.extentCenterY.get(i))) {
                        for(int p = MapInfoParser.doCollide.size()-1; p >=0; p--) {
                            if (Tile.mapDataType.get(i) == MapInfoParser.doCollide.get(p)) {
                                cameraX -= 0.02f;
                                break;
                            }
                            if(Tile.mapDataType.get(i) == '1'){
                                collisionType = 1;
                                break;
                            }

                        }
                    }
                }
            }
        }



        switch (collisionType){
            case 1:
                cameraSpeed = 0.005f;
            default:

        }
        collisionType = 0;


    }

    private static void handleMovement(float opX, float opY){
            cameraX = cameraX + ((cameraSpeed/ 2)*opX);
            cameraY = cameraY + ((cameraSpeed/ 2)*opY);
    }

    private static void processInput(long window) {
        //Sprinting8
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cameraSpeed = 0.1f;
            setIsSprinting(true);
        }
        else{
            setIsSprinting(false);
        }

        cameraSpeed = 0.01f;
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