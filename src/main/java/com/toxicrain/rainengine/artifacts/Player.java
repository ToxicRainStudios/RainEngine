package com.toxicrain.rainengine.artifacts;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.datatypes.*;
import com.toxicrain.rainengine.core.eventbus.events.load.MapLoadEvent;
import com.toxicrain.rainengine.core.json.GameInfoParser;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.core.interfaces.IArtifact;
import com.toxicrain.rainengine.core.json.key.KeyMap;
import com.toxicrain.rainengine.core.registries.tiles.Collisions;
import com.toxicrain.rainengine.core.render.BatchRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureRegion;
import com.toxicrain.rainengine.texture.TextureSystem;
import com.toxicrain.rainengine.util.InputUtils;
import com.toxicrain.rainengine.util.MathUtils;
import com.toxicrain.rainengine.util.WindowUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class Player extends RenderableArtifact implements IArtifact {

    @Getter @Setter private TextureRegion defaultTexture;
    @Getter @Setter private TextureInfo selectedTexture;

    private boolean isSprinting;

    public float scrollOffset;
    private float cameraSpeed = 0.02f;
    private final float scrollSpeed = 0.5f;

    private float[] openglMousePos;

    @Getter private final AABB playerAABB;

    @Getter @Setter private float angle;

    public Player(Resource defaultTexture) {
        super(defaultTexture, MapInfoParser.getInstance().playerSpawnPos.x, MapInfoParser.getInstance().playerSpawnPos.y, 0f);
        this.position.z = 5; // Player z-level
        this.defaultTexture = TextureSystem.getInstance().getRegion(defaultTexture);
        this.isSprinting = false;

        float playerHalfSize = Size.AVERAGE.getSize();

        this.playerAABB = new AABB(
                position.x - playerHalfSize,
                position.y - playerHalfSize,
                position.x + playerHalfSize,
                position.y + playerHalfSize
        );

        KeyMap.registerKeyBind(KeyMap.getKeyNumber("keyPause"),
                () -> glfwSetWindowShouldClose(GameEngine.windowManager.window, true));


        SmeagleBus.getInstance().listen(MapLoadEvent.class)
                .subscribe(event -> {
                    this.position.set(event.playerSpawnPos.x,event.playerSpawnPos.y, position.z);
                });
    }

    private float getAngle(float targetX, float targetY) {
        float dx = targetX - position.x;
        float dy = targetY - position.y;
        this.angle = (float) Math.atan2(dy, dx);
        return this.angle;
    }

    private void forward(int direction, double deltaTime) {
        getMouse();
        float rot = (float)Math.atan2((openglMousePos[1] - position.y),(openglMousePos[0] - position.x));
        position.x += (float) (Math.cos(rot)* 9.3f * direction * deltaTime);
        position.y += (float) (Math.sin(rot)* 9.3f * direction * deltaTime);
    }
    private void strafe(int direction, double deltaTime) {
        getMouse();
        float rot = (float)Math.atan2((openglMousePos[1] - position.y),(openglMousePos[0] - position.x));
        position.x += (float) (Math.cos(rot+Math.PI/2)* 9.3f * direction * deltaTime);
        position.y += (float) (Math.sin(rot+Math.PI/2)* 9.3f * direction * deltaTime);
    }

    public void update(double deltaTime) {
        super.update(deltaTime);
        getMouse();
        processInput(deltaTime);
    }

    float[] getMouse() {
        openglMousePos = InputUtils.mouseTracker.update(position);
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
        float halfSize = GameInfoParser.getInstance().playerSize / 2.0f;

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

        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkForward"))) {
            forward(true, 1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkBackward"))) {
            forward(-1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkLeft"))) {
            strafe(1, deltaTime);
        }
        if (GameFactory.inputUtils.isKeyPressed(KeyMap.getKeyNumber("keyWalkRight"))) {
            strafe(-1, deltaTime);
        }

        position.z = MathUtils.clamp(position.z + scrollOffset * scrollSpeed, GameInfoParser.getInstance().minZoom, GameInfoParser.getInstance().maxZoom);
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
}
