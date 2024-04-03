package util.movement;

import util.constant.Constants;

public class Camera {
    public static float aspectRatio = Constants.windowWidth / Constants.windowHeight; // Adjust this according to your window size
    public static float fov = 45.0f; // Field of view
    public static  float near = 0.1f; // Near clipping plane
    public static float far = 100.0f; // Far clipping plane
    public static float top = near * (float) Math.tan(Math.toRadians(fov / 2));
    public static float bottom = -top;
    public static float right = top * aspectRatio;
    public static float left = -right;

}