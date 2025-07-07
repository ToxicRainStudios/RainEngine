package com.toxicrain.rainengine.util;

import com.toxicrain.rainengine.core.GameEngine;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.factories.GameFactory;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public class WindowUtils{
    static FloatBuffer projMatrixBuffer = GameEngine.getPerspectiveProjectionMatrixBuffer();


    public static Vector3f getCenter() {
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.set(projMatrixBuffer);

        // Set up the view matrix
        Matrix4f viewMatrix = new Matrix4f().identity().translate(-GameFactory.player.getPosition().x, -GameFactory.player.getPosition().y, -GameFactory.player.getPosition().z);

        // Calculate the combined projection and view matrix
        Matrix4f projectionViewMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
        Matrix4f invProjectionViewMatrix = new Matrix4f(projectionViewMatrix).invert();

        // Get the center of the screen in window coordinates
        float screenX = SettingsInfoParser.getInstance().getWindowWidth() / 2.0f;
        float screenY = SettingsInfoParser.getInstance().getWindowHeight() / 2.0f;

        // Convert window coordinates to NDC (Normalized Device Coordinates)
        float ndcX = (2.0f * screenX) / SettingsInfoParser.getInstance().getWindowWidth() - 1.0f;
        float ndcY = 1.0f - (2.0f * screenY) / SettingsInfoParser.getInstance().getWindowHeight();

        // Convert NDC to world coordinates
        Vector4f ndcPos = new Vector4f(ndcX, ndcY, -1.0f, 1.0f).mul(invProjectionViewMatrix);

        return new Vector3f(ndcPos.x, ndcPos.y, ndcPos.z).div(ndcPos.w);
    }


}