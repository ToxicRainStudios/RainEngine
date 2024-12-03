package com.toxicrain.artifacts;

import com.toxicrain.core.AABB;
import com.toxicrain.core.GameEngine;
import com.toxicrain.core.Logger;
import com.toxicrain.sound.SoundSystem;
import com.toxicrain.texture.TextureInfo;
import com.toxicrain.core.interfaces.IArtifact;
import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.KeyInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.json.SettingsInfoParser;
import com.toxicrain.core.render.BatchRenderer;
import com.toxicrain.factories.GameFactory;
import com.toxicrain.core.Color;
import com.toxicrain.texture.TextureSystem;
import com.toxicrain.util.MathUtils;
import com.toxicrain.util.MouseUtils;
import com.toxicrain.util.WindowUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * The Player class provides information about the player
 */
public class Player implements IArtifact {

    @Getter @Setter
    private float posX;
    @Setter @Getter
    private float posY;
    @Setter @Getter
    private float posZ;
    @Getter @Setter
    private TextureInfo texture;
    private boolean isSprinting;
    public float cameraX, cameraY, cameraZ = 2; // Default camera Z
    private float prevCameraX, prevCameraY;
    public float scrollOffset;
    private float cameraSpeed = 0.02f; // Camera Speed
    private final float scrollSpeed = 0.5f; // Max scroll speed
    private final List<Weapon> weapons;
    private Weapon equippedWeapon;
    private float[] openglMousePos;

    public Player(float posX, float posY, float posZ, TextureInfo texture, boolean isSprinting) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
        this.isSprinting = isSprinting;
        this.weapons = new ArrayList<>();
        this.cameraX = MapInfoParser.playerx;
        this.cameraY = MapInfoParser.playery;
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }

    public void equipWeapon(Weapon weapon) {
        if (weapon != null && weapons.contains(weapon)) {
            if (equippedWeapon != null) {
                equippedWeapon.unequip();
            }
            equippedWeapon = weapon;
            equippedWeapon.equip();
            Logger.printLOG("Equipped weapon: " + weapon.getName());
        } else {
            Logger.printLOG("Weapon not found in inventory.");
        }
    }

    public void attack() {
        if (equippedWeapon != null) {
            equippedWeapon.attack();
        } else {
            Logger.printLOG("No weapon equipped.");
        }
    }

    private float getAngle(float[] mousePos) {
        float dx = mousePos[0] - posX;
        float dy = mousePos[1] - posY;
        return (float) Math.atan2(dy, dx);
    }

    private void forward(boolean useMouse, int direction) {
        getMouse();
        float angle = getAngle(openglMousePos);
        float angleXS = (float) Math.sin(angle) * -1;
        float angleYS = (float) Math.cos(angle);

        if (useMouse) {
            cameraX += (openglMousePos[0] - posX) * 0.01f * direction;
            cameraY += (openglMousePos[1] - posY) * 0.01f * direction;
        } else {
            cameraX += angleXS * 0.007f * direction;
            cameraY += angleYS * 0.007f * direction;
        }
    }

    public void update(float deltaTime) {
        processInput();
        updatePos(cameraX, cameraY, cameraZ);
        Vector3f center = WindowUtils.getCenter();

        // Calculate velocity based on deltaTime
        float velocityX = (cameraX - prevCameraX) / deltaTime;
        float velocityY = (cameraY - prevCameraY) / deltaTime;

        // Update previous position
        prevCameraX = cameraX;
        prevCameraY = cameraY;
    }

    private void getMouse() {
        float[] mousePos = GameFactory.mouseUtils.getMousePosition();
        openglMousePos = MouseUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowWidth(), cameraX, cameraY);
    }

    public void render(BatchRenderer batchRenderer) {
        getMouse();
        Vector3f center = WindowUtils.getCenter();
        batchRenderer.addTexturePos(TextureSystem.getTexture("playerTextureRifle"), center.x, center.y, 1.1f, openglMousePos[0],
                openglMousePos[1], 1, 1, Color.toFloatArray(Color.WHITE));
    }

    private void handleCollisions() {
        int collisionType = 0;
        float playerHalfSize = GameInfoParser.playerSize / 2.0f;

        // Create player's AABB based on its position and size
        AABB playerAABB = new AABB(
                cameraX - playerHalfSize, // minX
                cameraY - playerHalfSize, // minY
                cameraX + playerHalfSize, // maxX
                cameraY + playerHalfSize  // maxY
        );
        for (int i = Tile.aabbs.size()- 1; i >= 0; i--) {

            // Push player back slightly based on collision direction
            if (Collisions.collide(playerAABB, i) == 'u') {
                // Colliding from below
                cameraY += 0.02f;
            } else if (Collisions.collide(playerAABB, i) == 'd') {
                // Colliding from above
                cameraY -= 0.02f;
            }

            if (Collisions.collide(playerAABB, i) == 'l') {
                // Colliding from the left
                cameraX += 0.02f;
            } else if (Collisions.collide(playerAABB, i) == 'r') {
                // Colliding from the right
                cameraX -= 0.02f;
            }
        }

    }


    private void processInput() {
        handleSprinting();
        handleCollisions();
        handleMovement();
        handleAttack();

        // Update cameraZ based on the scroll input
        cameraZ = MathUtils.clamp(cameraZ + scrollOffset * scrollSpeed, GameInfoParser.minZoom, GameInfoParser.maxZoom);
        scrollOffset = 0.0f;
    }

    private void handleSprinting() {
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keySprint"))) {
            cameraSpeed = 0.1f;
            isSprinting = true;
        } else {
            isSprinting = false;
            cameraSpeed = 0.01f;
        }
    }

    private void handleMovement() {
        if(!GameEngine.menu){
            if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkLeft"))) forward(false, 1);
            if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkRight"))) forward(false, -1);
            if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkForward"))) forward(true, 1);
            if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkBackward"))) forward(true, -1);
        }
    }

    private void handleAttack() {
        if (GameFactory.mouseUtils.isMouseButtonPressed(1)) {
            GameFactory.soundSystem.play(SoundSystem.getSound("Sample"));
            Logger.printLOG("Player is attacking...");
            attack();
        }
    }

    // Method to update position of the player
    private void updatePos(float x, float y, float z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

}
