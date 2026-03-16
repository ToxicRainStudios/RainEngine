package com.toxicrain.rainengine.artifacts;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f rotation; // pitch, yaw, roll in degrees

    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    private final float fov;
    private float aspectRatio;
    private final float nearPlane;
    private final float farPlane;

    public Camera(float fov, float aspectRatio, float nearPlane, float farPlane) {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;

        updateProjectionMatrix();
        updateViewMatrix();
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        updateViewMatrix();
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
        updateViewMatrix();
    }

    public void move(Vector3f offset) {
        this.position.add(offset);
        updateViewMatrix();
    }

    public void rotate(Vector3f delta) {
        this.rotation.add(delta);
        updateViewMatrix();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void updateProjectionMatrix() {
        projectionMatrix.identity();
        projectionMatrix.perspective((float)Math.toRadians(fov), aspectRatio, nearPlane, farPlane);
    }

    public void updateViewMatrix() {
        viewMatrix.identity();
        // Apply rotation (pitch = x, yaw = y, roll = z)
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1,0,0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1,0))
                .rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1));
        // Apply translation
        viewMatrix.translate(-position.x, -position.y, -position.z);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        updateProjectionMatrix();
    }


    // Convenience methods
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
}
