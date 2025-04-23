package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.AABB;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.registries.WeaponRegistry;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.KeyInfoParser;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.core.Color;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.MathUtils;
import com.toxicrain.rainengine.util.InputUtils;
import com.toxicrain.rainengine.util.WindowUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * The Player class provides information about the player
 */
public class Player implements IArtifact { //TODO this needs a de-spaghettification

    @Getter @Setter
    private TextureInfo defaultTexture;
    private TextureInfo selectedTexture;
    private boolean isSprinting;
    public TilePos playerPos;
    public float cameraX, cameraY, cameraZ = 2; // Default camera Z
    public float scrollOffset;
    private float cameraSpeed = 0.02f; // Camera Speed
    private final float scrollSpeed = 0.5f; // Max scroll speed
    private final List<Weapon> weapons;
    @Getter
    private Weapon equippedWeapon;
    private float[] openglMousePos;
    private final AABB playerAABB;

    // New variable to hold the player's angle
    @Getter @Setter
    private float angle;

    public Player(float posX, float posY, float posZ, TextureInfo defaultTexture, boolean isSprinting) {
        this.playerPos = new TilePos(posX, posY, posZ);
        this.defaultTexture = defaultTexture;
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
        }
    }

    public boolean isWeaponEquipped(Weapon weapon) {
        return equippedWeapon != null && equippedWeapon.equals(weapon);
    }


    public void attack() {
        if (equippedWeapon != null) {
            // Get OpenGL mouse coordinates, as used in rendering
            float[] openglMousePos = InputUtils.convertToOpenGLCoordinates(
                    GameFactory.inputUtils.getMousePosition()[0],
                    GameFactory.inputUtils.getMousePosition()[1],
                    (int) SettingsInfoParser.getInstance().getWindowWidth(),
                    (int) SettingsInfoParser.getInstance().getWindowHeight()
            );

            // Convert to world coordinates by factoring in the camera position
            float worldMouseX = openglMousePos[0] + cameraX;
            float worldMouseY = openglMousePos[1] + cameraY;

            // Use the same angle as rendering
            float playerAngle = getAngle(worldMouseX, worldMouseY);

            equippedWeapon.attack(playerAngle, playerPos.x, playerPos.y);
        }
    }

    private float getAngle(float targetX, float targetY) {
        float dx = targetX - playerPos.x;
        float dy = targetY - playerPos.y;
        this.angle = (float) Math.atan2(dy, dx);
        return this.angle;
    }

    private void forward(boolean useMouse, int direction, float deltaTime) {
        getMouse();

        float angleXS = (float) Math.sin(angle) * -1;
        float angleYS = (float) Math.cos(angle);

        if (useMouse) {
            cameraX += (openglMousePos[0] - playerPos.x) * 9.3f * direction * deltaTime;
            cameraY += (openglMousePos[1] - playerPos.y) * 9.3f * direction * deltaTime;
        } else {
            cameraX += angleXS * 4.2f * direction * deltaTime;
            cameraY += angleYS * 4.2f * direction * deltaTime;
        }
    }

    public void update(float deltaTime) {
        getMouse();
        processInput(deltaTime);

        if (isWeaponEquipped(WeaponRegistry.get("Pistol"))){
            selectedTexture = TextureSystem.getTexture("playerTexturePistol");
        }
        if (isWeaponEquipped(WeaponRegistry.get("Rifle"))){
            selectedTexture = TextureSystem.getTexture("playerTextureRifle");
        }
        if (isWeaponEquipped(WeaponRegistry.get("Shotgun"))){
            selectedTexture = TextureSystem.getTexture("playerTextureShotgun");
        }
        else {
            selectedTexture = defaultTexture;
        }


       playerPos.update(cameraX, cameraY, cameraZ);
    }

    float[] getMouse() {
        float[] mousePos = GameFactory.inputUtils.getMousePosition();
        openglMousePos = InputUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), cameraX, cameraY);
        return openglMousePos;
    }

    public void render(BatchRenderer batchRenderer) {
        Vector3f center = WindowUtils.getCenter();
        batchRenderer.addTexturePos(selectedTexture, center.x, center.y, 1.1f, openglMousePos[0],
                openglMousePos[1], 1, 1, Color.toFloatArray(Color.WHITE));
    }

    private void handleCollisions(float deltaTime) {
        float halfSize = GameInfoParser.playerSize / 2.0f; //TODO Size

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
                cameraY += 9.3f*deltaTime;
                break;
            case 'd':
                // Colliding from above
                cameraY -= 9.3f*deltaTime;
                break;
            case 'l':
                // Colliding from the left
                cameraX += 9.3f*deltaTime;
                break;
            case 'r':
                // Colliding from the right
                cameraX -= 9.3f*deltaTime;
                break;
        }

    }

    private void processInput(float deltaTime) {
        handleSprinting();
        handleCollisions(deltaTime);
        handleMovement(deltaTime);
        handleAttack();

        // Update cameraZ based on the scroll input
        cameraZ = MathUtils.clamp(cameraZ + scrollOffset * scrollSpeed, GameInfoParser.minZoom, GameInfoParser.maxZoom);
        scrollOffset = 0.0f;
    }

    private void handleSprinting() {
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keySprint"))) {
            cameraSpeed = 0.1f;
            isSprinting = true;
        } else {
            isSprinting = false;
            cameraSpeed = 0.01f;
        }
    }

    private void handleMovement(float deltaTime) {
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkLeft"))) forward(false, 1,deltaTime);
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkRight"))) forward(false, -1,deltaTime);
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkForward"))) forward(true, 1,deltaTime);
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWalkBackward"))) forward(true, -1,deltaTime);
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyWeaponOne"))) equipWeapon(WeaponRegistry.get("Shotgun"));
        if (GameFactory.inputUtils.isKeyPressed(KeyInfoParser.getKeyAsGLWFBind("keyReloadTextures"))) TextureSystem.reloadTextures();
    }

    private void handleAttack() {
        if (GameFactory.inputUtils.isMouseButtonPressed(0)) {
            attack();
        }
    }
}
