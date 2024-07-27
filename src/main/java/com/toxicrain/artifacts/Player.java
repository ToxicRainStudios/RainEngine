package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.render.Tile;
import com.toxicrain.util.CollisionUtils;
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
    private static float prevCameraX;
    private static float prevCameraY;
    public static float velocityX;
    public static float velocityY;
    public static float cameraSpeed = 0.02f; // Camera Speed
    public static final float scrollSpeed = 0.5f;  // The max scroll in/out speed
    public static float scrollOffset = 0.0f; // Track the scroll input
    public static Vector3f center;
    public static MouseUtils mouseInput = new MouseUtils(window);
    public static float angleX;
    public static float angleY;
    public static float angleXS;
    public static float angleYS;
    private static CollisionUtils col;



    private static void getAngle(){
        getMouse();
        float dx = openglMousePos[0] - posX;
        float dy = openglMousePos[1] - posY;
        float angle = (float) Math.atan2(dy, dx);
        angleX= (float)Math.cos(angle);
        angleY = (float)Math.sin(angle);
        angleXS= (float)Math.sin(angle)*-1;
        angleYS = (float)Math.cos(angle);

    }
    public Player(float posX, float posY, float posZ, TextureInfo texture, boolean isSprinting) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
        this.isSprinting = isSprinting;
    }

//this is temp code  \|/
    private static void forward(boolean true1, int direction){
        getAngle();
        if(true1) {
            cameraX = cameraX + (openglMousePos[0]-posX) * 0.01f*direction;
            cameraY = cameraY + (openglMousePos[1]-posY) * 0.01f*direction;
        }
        else{
            cameraX = cameraX + angleXS * 0.007f*direction;
            cameraY = cameraY + angleYS * 0.007f*direction;
        }


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

        velocityX = (cameraX - prevCameraX);
        velocityY = (cameraY - prevCameraY);

        // Update previous position
        prevCameraX = cameraX;
        prevCameraY = cameraY;
    }

    private static float[] openglMousePos;
    private static void getMouse(){
        float[] mousePos = mouseInput.getMousePosition();
        openglMousePos = MouseUtils.convertToOpenGLCoordinates(mousePos[0], mousePos[1], (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight);
    }
    public static void render(BatchRenderer batchRenderer){
        // Convert mouse coordinates to OpenGL coordinates
        getMouse();

        batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], 1,1, Color.toFloatArray(1.0f, Color.WHITE));
    }


    private static void handleCollisions(){
        if(col == null) {
            col = new CollisionUtils();
        }
        for (int j = 1; j > -2; j -= 1) {
            k = (float) j * GameInfoParser.playerSize;
            for (int i = Tile.extentTop.size() - 1; i >= 0; i--) {
                col.handleCollisions(j,col,cameraX,cameraY,1f,Tile.extentCenterX.get(i),Tile.extentCenterY.get(i),Tile.mapDataType.get(i));
                cameraX += col.changePosX;
                cameraY += col.changePosY;
            }
        }
        switch (collisionType){
            case 1:
                cameraSpeed = 0.010f;
            default:

        }
        collisionType = 0;


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

        cameraSpeed = 0.005f;
        handleCollisions();

        // Handle left and right movement
        if((glfwGetKey(window, GLFW_KEY_A ) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)){
            cameraX -= cameraSpeed/2;
            cameraY += cameraSpeed/2;
        }
        else if((glfwGetKey(window, GLFW_KEY_D ) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)){
            cameraX += cameraSpeed/2;
            cameraY += cameraSpeed/2;
        }
       else if((glfwGetKey(window, GLFW_KEY_A ) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)){
            cameraX -= cameraSpeed/2;
            cameraY -= cameraSpeed/2;
        }
        else if((glfwGetKey(window, GLFW_KEY_D ) == GLFW_PRESS && glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)){
            cameraX += cameraSpeed/2;
            cameraY -= cameraSpeed/2;
        }
       else if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)cameraX -= cameraSpeed;
       else if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)cameraX += cameraSpeed;
       else if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)cameraY += cameraSpeed;
       else if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)cameraY -= cameraSpeed;



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