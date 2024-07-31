package com.toxicrain.artifacts;

import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.KeyInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.core.render.Tile;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.util.Color;
import com.toxicrain.util.MouseUtils;
import com.toxicrain.util.WindowUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static com.toxicrain.core.GameEngine.window;
import static com.toxicrain.util.TextureUtils.playerTexture;


/**
 * The Player class provides information about the player
 *
 * @author Gabefry, strubium
 */
public class Player implements IArtifact {

    // Existing attributes
    public static float posX;
    public static float posY;
    public static float posZ;
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
    public static float angleX;
    public static float angleY;
    public static float angleXS;
    public static float angleYS;

    // Weapon-related attributes
    private List<Weapon> weapons;
    private static Weapon equippedWeapon;

    public Player(float posX, float posY, float posZ, TextureInfo texture, boolean isSprinting) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
        this.isSprinting = isSprinting;
        this.weapons = new ArrayList<>();
        this.equippedWeapon = null;
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    public void equipWeapon(Weapon weapon) {
        if (weapon != null) {
            if (weapons.contains(weapon)) {
                if (equippedWeapon != null) {
                    equippedWeapon.unequip();
                }
                equippedWeapon = weapon;
                equippedWeapon.equip();
                System.out.println("Equipped weapon: " + weapon.getName());
            } else {
                System.out.println("Weapon not found in inventory.");
            }
        }
    }

    public static void attack() {
        if (equippedWeapon != null) {
            equippedWeapon.attack();
        } else {
            System.out.println("No weapon equipped.");
        }
    }



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
        float[] mousePos = GameFactory.mouseUtils.getMousePosition();
        openglMousePos = MouseUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1], (int) SettingsInfoParser.windowWidth, (int) SettingsInfoParser.windowHeight, Player.cameraX, Player.cameraY);
    }
    public static void render(BatchRenderer batchRenderer){
        // Convert mouse coordinates to OpenGL coordinates
        getMouse();

        batchRenderer.addTexturePos(playerTexture, center.x, center.y, 1.1f, openglMousePos[0], openglMousePos[1], 1,1, Color.toFloatArray(Color.WHITE));
    }


    private static void handleCollisions(){
        for (int j = 1; j > -2; j -= 1) {
            float k = (float) j * GameInfoParser.playerSize;
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
                cameraSpeed = 0.010f;
            default:

        }
        collisionType = 0;


    }



    private static void processInput(long window) {
        // Sprinting
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keySprint))) {
            cameraSpeed = 0.1f;
            setIsSprinting(true);
        } else {
            setIsSprinting(false);
        }

        cameraSpeed = 0.01f;
        handleCollisions();


        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyWeaponOne))) {
            GameFactory.player.equipWeapon(GameFactory.pistol);
        }


        // Handle left and right movement
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyWalkLeft))) forward(false, 1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyWalkRight))) forward(false, -1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyWalkForward))) forward(true, 1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyWalkBackward))) forward(true, -1);

        // Handle weapon attack
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.convertToGLFWBind(KeyInfoParser.keyAttack))) {
            attack();
        }

        // Update cameraZ based on the scroll input
        cameraZ += scrollOffset * scrollSpeed;

        // Clamp cameraZ at max 25 and min 3
        cameraZ = Math.max(GameInfoParser.minZoom, Math.min(cameraZ, GameInfoParser.maxZoom));

        scrollOffset = 0.0f; // Reset the scroll offset after applying it
    }
}