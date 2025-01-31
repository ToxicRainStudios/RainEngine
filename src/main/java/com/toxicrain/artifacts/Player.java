package com.toxicrain.artifacts;

import com.toxicrain.core.AABB;
import com.toxicrain.core.RainLogger;
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
    private AABB playerAABB;

    // New variable to hold the player's angle
    @Getter @Setter
    private float angle;

    public Player(float posX, float posY, float posZ, TextureInfo texture, boolean isSprinting) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.texture = texture;
        this.isSprinting = isSprinting;
        this.weapons = new ArrayList<>();
        this.cameraX = MapInfoParser.playerx;
        this.cameraY = MapInfoParser.playery;

        float playerHalfSize = Size.AVERAGE.getSize();

        // Create player's AABB based on its position and size
        this.playerAABB = new AABB(
                cameraX - playerHalfSize, // minX
                cameraY - playerHalfSize, // minY
                cameraX + playerHalfSize, // maxX
                cameraY + playerHalfSize  // maxY
        );
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
            RainLogger.printLOG("Equipped weapon: " + weapon.getName());
        } else {
            RainLogger.printLOG("Weapon not found in inventory.");
        }
    }

    public void attack() {
        if (equippedWeapon != null) {
            // Get OpenGL mouse coordinates, as used in rendering
            float[] openglMousePos = MouseUtils.convertToOpenGLCoordinates(
                    GameFactory.mouseUtils.getMousePosition()[0],
                    GameFactory.mouseUtils.getMousePosition()[1],
                    (int) SettingsInfoParser.getInstance().getWindowWidth(),
                    (int) SettingsInfoParser.getInstance().getWindowHeight()
            );

            // Convert to world coordinates by factoring in the camera position
            float worldMouseX = openglMousePos[0] + cameraX;
            float worldMouseY = openglMousePos[1] + cameraY;

            // Use the same angle as rendering
            float playerAngle = getAngle(worldMouseX, worldMouseY);

            equippedWeapon.attack(playerAngle, posX, posY);
        }
    }

    private float getAngle(float[] mousePos) {
        // Calculate the angle based on the mouse position and player's position
        float dx = mousePos[0] - posX;
        float dy = mousePos[1] - posY;
        this.angle = (float) Math.atan2(dy, dx);  // Store the angle in the field
        return this.angle;
    }
    private float getAngle(float targetX, float targetY) {
        float dx = targetX - posX;
        float dy = targetY - posY;
        this.angle = (float) Math.atan2(dy, dx);
        return this.angle;
    }

    private void forward(boolean useMouse, int direction) {
        getMouse();
        // Now use the stored angle
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
        getMouse();
        processInput();
        updatePos(cameraX, cameraY, cameraZ);

        // Calculate velocity based on deltaTime
        float velocityX = (cameraX - prevCameraX) / deltaTime;
        float velocityY = (cameraY - prevCameraY) / deltaTime;

        // Update previous position
        prevCameraX = cameraX;
        prevCameraY = cameraY;
    }

    float[] getMouse() {
        float[] mousePos = GameFactory.mouseUtils.getMousePosition();
        openglMousePos = MouseUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), cameraX, cameraY);
        return openglMousePos;
    }

    public void render(BatchRenderer batchRenderer) {
        Vector3f center = WindowUtils.getCenter();
        batchRenderer.addTexturePos(TextureSystem.getTexture("playerTextureRifle"), center.x, center.y, 1.1f, openglMousePos[0],
                openglMousePos[1], 1, 1, Color.toFloatArray(Color.WHITE));
    }

    private void handleCollisions() {
        float halfSize = GameInfoParser.playerSize / 2.0f;

        // Update npc's AABB based on its position and size
        this.playerAABB.update(
                cameraX - halfSize,
                cameraY - halfSize,
                cameraX + halfSize,
                cameraY + halfSize
        );

        char collisionDirection = Collisions.collideWorld(this.playerAABB);

        // Handle the collision direction with a switch statement
        switch (collisionDirection) {
            case 'u':
                // Colliding from below
                cameraY += 0.02f;
                break;
            case 'd':
                // Colliding from above
                cameraY -= 0.02f;
                break;
            case 'l':
                // Colliding from the left
                cameraX += 0.02f;
                break;
            case 'r':
                // Colliding from the right
                cameraX -= 0.02f;
                break;
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
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkLeft"))) forward(false, 1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkRight"))) forward(false, -1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkForward"))) forward(true, 1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkBackward"))) forward(true, -1);
        if (GameFactory.mouseUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWeaponOne"))) equipWeapon(GameFactory.shotgun);
    }

    private void handleAttack() {
        if (GameFactory.mouseUtils.isMouseButtonPressed(0)) {
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
