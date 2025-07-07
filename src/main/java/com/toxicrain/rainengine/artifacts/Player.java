package com.toxicrain.rainengine.artifacts;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.datatypes.*;
import com.toxicrain.rainengine.core.eventbus.events.ArtifactUpdateEvent;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.util.InputUtils;
import com.toxicrain.rainengine.util.MathUtils;
import com.toxicrain.rainengine.util.WindowUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class Player extends RenderableArtifact implements IArtifact {

    @Getter @Setter private TextureRegion defaultTexture;
    @Getter @Setter private TextureInfo selectedTexture;

    private boolean isSprinting;

    public float scrollOffset;
    private float cameraSpeed = 0.02f;
    private final float scrollSpeed = 0.5f;

    private final List<Weapon> weapons;
    @Getter private Weapon equippedWeapon;

    private float[] openglMousePos;

    private final AABB playerAABB;

    @Getter @Setter private float angle;

    public Player(Resource defaultTexture, boolean isSprinting) {
        super(defaultTexture, MapInfoParser.playerx, MapInfoParser.playery, 0f, 1f);
        this.position.z = 5; // Player z-level
        this.defaultTexture = GameFactory.textureAtlas.getRegion(defaultTexture);
        this.isSprinting = isSprinting;
        this.weapons = new ArrayList<>();

        float playerHalfSize = Size.AVERAGE.getSize();

        this.playerAABB = new AABB(
                position.x - playerHalfSize,
                position.y - playerHalfSize,
                position.x + playerHalfSize,
                position.y + playerHalfSize
        );

        KeyMap.registerKeyBind(KeyMap.getKeyNumber("keyPause"),
                () -> glfwSetWindowShouldClose(GameEngine.windowManager.window, true));
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
            float[] openglMousePos = InputUtils.convertToOpenGLCoordinates(
                    GameFactory.inputUtils.getMousePosition()[0],
                    GameFactory.inputUtils.getMousePosition()[1],
                    (int) SettingsInfoParser.getInstance().getWindowWidth(),
                    (int) SettingsInfoParser.getInstance().getWindowHeight()
            );

            float worldMouseX = openglMousePos[0] + position.x;
            float worldMouseY = openglMousePos[1] + position.y;

            float playerAngle = getAngle(worldMouseX, worldMouseY);

            equippedWeapon.attack(playerAngle, position.x, position.y);
        }
    }

    private float getAngle(float targetX, float targetY) {
        float dx = targetX - position.x;
        float dy = targetY - position.y;
        this.angle = (float) Math.atan2(dy, dx);
        return this.angle;
    }

    private void forward(boolean useMouse, int direction, double deltaTime) {
        getMouse();

        float angleXS = (float) Math.sin(angle) * -1;
        float angleYS = (float) Math.cos(angle);
        double distanceOfMouse = Math.sqrt(Math.pow(openglMousePos[0] - position.x, 2) + Math.pow(openglMousePos[1] - position.y, 2));

        if (useMouse) {
            position.x += ((openglMousePos[0] - position.x) / distanceOfMouse) * 9.3f * direction * deltaTime;
            position.y += ((openglMousePos[1] - position.y) / distanceOfMouse) * 9.3f * direction * deltaTime;
        } else {
            position.x += angleXS * 5.2f * direction * deltaTime;
            position.y += angleYS * 5.2f * direction * deltaTime;
        }
    }

    public void update(double deltaTime) {
        getMouse();
        processInput(deltaTime);

        SmeagleBus.getInstance().post(new ArtifactUpdateEvent("player"));
    }

    float[] getMouse() {
        float[] mousePos = GameFactory.inputUtils.getMousePosition();
        openglMousePos = InputUtils.convertToOpenGLCoordinatesOffset(mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(),
                (int) SettingsInfoParser.getInstance().getWindowHeight(),
                position.x, position.y);
        return openglMousePos;
    }

    @Override
    public void render(BatchRenderer batchRenderer) {
        if (defaultTexture != null && openglMousePos != null) {
            Vector3f center = WindowUtils.getCenter();

            batchRenderer.addTexture(defaultTexture, center.x, center.y, 1.1f,
                    new TileParameters(null, openglMousePos[0], openglMousePos[1], 1f, 1f, null, LightSystem.getLightSources()));
        }
    }

    private void handleCollisions(float deltaTime) {
        float halfSize = GameInfoParser.playerSize / 2.0f;

        this.playerAABB.update(
                position.x - halfSize,
                position.y - halfSize,
                position.x + halfSize,
                position.y + halfSize
        );

        char collisionDirection = Collisions.collideWorld(this.playerAABB);

        switch (collisionDirection) {
            case 'u':
                position.y += 9.3f * deltaTime;
                break;
            case 'd':
                position.y -= 9.3f * deltaTime;
                break;
            case 'l':
                position.x += 9.3f * deltaTime;
                break;
            case 'r':
                position.x -= 9.3f * deltaTime;
                break;
        }
    }

    private void processInput(double deltaTime) {
        handleSprinting();
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

        position.z = MathUtils.clamp(position.z + scrollOffset * scrollSpeed, GameInfoParser.minZoom, GameInfoParser.maxZoom);
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
