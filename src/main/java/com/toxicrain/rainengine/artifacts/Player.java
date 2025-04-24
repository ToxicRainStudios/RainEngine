package com.toxicrain.rainengine.artifacts;

import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.RainLogger;
import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.datatypes.Size;
import com.toxicrain.rainengine.core.datatypes.TileParameters;
import com.toxicrain.rainengine.core.datatypes.TilePos;
import com.toxicrain.rainengine.core.eventbus.events.ArtifactUpdateEvent;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.core.registries.WeaponRegistry;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.MathUtils;
import com.toxicrain.rainengine.util.InputUtils;
import com.toxicrain.rainengine.util.WindowUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * The Player class provides information about the player
 */
public class Player implements IArtifact { //TODO this needs a de-spaghettification

    @Getter @Setter
    private TextureInfo defaultTexture;
    @Getter @Setter
    private TextureInfo selectedTexture;
    private boolean isSprinting;
    public TilePos playerPos;
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

    public Player(TextureInfo defaultTexture, boolean isSprinting) {
        this.playerPos = new TilePos(MapInfoParser.playerx, MapInfoParser.playery, 5);
        this.defaultTexture = defaultTexture;
        this.isSprinting = isSprinting;
        this.weapons = new ArrayList<>();

        float playerHalfSize = Size.AVERAGE.getSize();

        // Create player's AABB based on its position and size
        this.playerAABB = new AABB(
                playerPos.x - playerHalfSize, // minX
                playerPos.y - playerHalfSize, // minY
                playerPos.x + playerHalfSize, // maxX
                playerPos.y + playerHalfSize  // maxY
        );

        KeyMap.registerKeyBind(KeyMap.getKeyNumber("keyPause"), () ->  glfwSetWindowShouldClose(GameEngine.windowManager.window, true));
        KeyMap.registerKeyBind(KeyMap.getKeyNumber("keyReloadTextures"), TextureSystem::reloadTextures);
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
            float worldMouseX = openglMousePos[0] + playerPos.x;
            float worldMouseY = openglMousePos[1] + playerPos.y;

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
        double distanceOfMouse = Math.sqrt(Math.pow(openglMousePos[0] - playerPos.x,2)+Math.pow(openglMousePos[1] - playerPos.y,2));
        if (useMouse) {
            playerPos.x += ((openglMousePos[0] - playerPos.x)/distanceOfMouse) * 9.3f * direction * deltaTime;
            playerPos.y += ((openglMousePos[1] - playerPos.y)/distanceOfMouse) * 9.3f * direction * deltaTime;
        } else {
            playerPos.x += angleXS * 5.2f * direction * deltaTime;
            playerPos.y += angleYS * 5.2f * direction * deltaTime;
        }
    }

    public void update(float deltaTime) {
        getMouse();
        processInput(deltaTime);

        GameFactory.eventBus.post(new ArtifactUpdateEvent("player"));
    }

    float[] getMouse() {
        float[] mousePos = GameFactory.inputUtils.getMousePosition();
        openglMousePos = InputUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(), (int) SettingsInfoParser.getInstance().getWindowHeight(), playerPos.x, playerPos.y);
        return openglMousePos;
    }

    public void render(BatchRenderer batchRenderer) {
        if (selectedTexture != null){
            Vector3f center = WindowUtils.getCenter();
            batchRenderer.addTexture(selectedTexture, center.x, center.y, 1.1f,
                    new TileParameters(null, openglMousePos[0],openglMousePos[1], 1f,1f, null, LightSystem.getLightSources()));
        }
        else{
            RainLogger.RAIN_LOGGER.info("null selectedTexture!");
        }
    }

    private void handleCollisions(float deltaTime) {
        float halfSize = GameInfoParser.playerSize / 2.0f; //TODO Size

        // Update npc's AABB based on its position and size
        this.playerAABB.update(
                playerPos.x - halfSize,
                playerPos.y - halfSize,
                playerPos.x + halfSize,
                playerPos.x + halfSize
        );

        char collisionDirection = Collisions.collideWorld(this.playerAABB);

        // Handle the collision direction with a switch statement
        switch (collisionDirection) {
            case 'u':
                // Colliding from below
                playerPos.y += 9.3f*deltaTime;
                break;
            case 'd':
                // Colliding from above
                playerPos.y -= 9.3f*deltaTime;
                break;
            case 'l':
                // Colliding from the left
                playerPos.x += 9.3f*deltaTime;
                break;
            case 'r':
                // Colliding from the right
                playerPos.x -= 9.3f*deltaTime;
                break;
        }

    }

    private void processInput(float deltaTime) {
        handleSprinting();
        //handleCollisions(deltaTime);
        handleAttack();

        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkForward"))) {
            forward(true, 1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkBackward"))) {
            forward(true, -1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkLeft"))) {
            forward(false, 1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkRight"))) {
            forward(false, -1, deltaTime);
        }


        // Update cameraZ based on the scroll input
        playerPos.z = MathUtils.clamp(playerPos.z + scrollOffset * scrollSpeed, GameInfoParser.minZoom, GameInfoParser.maxZoom);
        scrollOffset = 0.0f;
    }

    private void handleSprinting() {
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keySprint"))) {
            cameraSpeed = 0.1f;
            isSprinting = true;
        } else {
            isSprinting = false;
            cameraSpeed = 0.01f;
        }
    }


    private void handleAttack() {
        if (GameFactory.inputUtils.isMouseButtonPressed(0)) {
            attack();
        }
    }
}
