package com.toxicrain.rainengine.core.json.key;

import com.toxicrain.rainengine.core.BaseInstanceable;
import com.toxicrain.rainengine.core.datatypes.vector.Vector3;
import com.toxicrain.rainengine.core.json.SettingsInfoParser;
import com.toxicrain.rainengine.util.InputUtils;
import com.toxicrain.rainengine.factories.GameFactory;

public class MouseTracker extends BaseInstanceable<MouseTracker> {
    private float[] openglMousePos = new float[]{0f, 0f};

    public static MouseTracker getInstance() {
        return BaseInstanceable.getInstance(MouseTracker.class);
    }

    public float[] update(float offsetX, float offsetY) {
        float[] mousePos = safeGetMousePosition();
        openglMousePos = InputUtils.convertToOpenGLCoordinatesOffset(
                mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(),
                (int) SettingsInfoParser.getInstance().getWindowHeight(),
                offsetX, offsetY
        );
        return openglMousePos;
    }

    public float[] update(Vector3 vector3) {
        float[] mousePos = safeGetMousePosition();
        openglMousePos = InputUtils.convertToOpenGLCoordinatesOffset(
                mousePos[0], mousePos[1],
                (int) SettingsInfoParser.getInstance().getWindowWidth(),
                (int) SettingsInfoParser.getInstance().getWindowHeight(),
                vector3.x, vector3.y
        );
        return openglMousePos;
    }

    public float[] getOpenGLMousePos() {
        return openglMousePos != null ? openglMousePos : new float[]{0f, 0f};
    }

    public float[] getRawMousePos() {
        return safeGetMousePosition();
    }

    private float[] safeGetMousePosition() {
        try {
            float[] mouse = GameFactory.inputUtils.getMousePosition();
            return mouse != null && mouse.length >= 2 ? mouse : new float[]{0f, 0f};
        } catch (Exception e) {
            return new float[]{0f, 0f};
        }
    }
}
