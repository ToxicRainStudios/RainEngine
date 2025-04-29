package com.toxicrain.rainengine;

import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.List;

public class FabrikSolver {

    public static class Joint {
        public Vector2f position;
        public Vector2f restPosition; // For resetting
        public Joint parent;
        public List<Joint> children = new ArrayList<>();
        public float lengthToParent;
        public float stiffness = 1.0f; // 1 = fully rigid, 0 = very floppy
        public float maxStretchFactor = 1.0f; // 1 = no stretch, 1.2 = 20% stretch allowed

        // New properties
        public float minAngle = -180f; // degrees, relative to parent
        public float maxAngle = 180f;  // degrees, relative to parent
        public float mass = 1.0f; // 1.0 = normal weight
        private String name;

        public Joint(Vector2f position, String name) {
            this.position = new Vector2f(position);
            this.restPosition = new Vector2f(position);
            this.name = name;
        }

        public void addChild(Joint child) {
            child.parent = this;
            child.lengthToParent = child.position.distance(this.position);
            children.add(child);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private Joint root;
    private int maxIterations = 10;
    private float tolerance = 0.001f;
    private boolean allowStretching = false;

    public FabrikSolver(Joint root) {
        this.root = root;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setTolerance(float tolerance) {
        this.tolerance = tolerance;
    }

    public void setAllowStretching(boolean allowStretching) {
        this.allowStretching = allowStretching;
    }

    public void solve(Joint endEffector, Vector2f target) {
        if (root == null) return;

        for (int i = 0; i < maxIterations; i++) {
            forward(endEffector, target);
            backward(root, root.position);
            if (endEffector.position.distance(target) <= tolerance) break;
        }
    }

    private void forward(Joint joint, Vector2f target) {
        joint.position.set(target);

        if (joint.parent != null) {
            Vector2f dir = new Vector2f(joint.parent.position).sub(joint.position);
            float distance = dir.length();
            dir.normalize();

            float desiredLength = joint.lengthToParent;
            if (allowStretching) {
                desiredLength *= joint.maxStretchFactor;
            }

            Vector2f newParentPos = new Vector2f(joint.position).add(dir.mul(desiredLength));

            // Mass influences stiffness (heavier = slower interpolation)
            float effectiveStiffness = joint.parent.stiffness / joint.parent.mass;
            effectiveStiffness = Math.min(Math.max(effectiveStiffness, 0f), 1f);

            joint.parent.position.lerp(newParentPos, 1.0f - effectiveStiffness);

            // Apply constraint
            applyConstraints(joint.parent);

            forward(joint.parent, joint.parent.position);
        }
    }

    private void backward(Joint joint, Vector2f position) {
        joint.position.set(position);

        for (Joint child : joint.children) {
            Vector2f dir = new Vector2f(child.position).sub(joint.position);
            float distance = dir.length();
            dir.normalize();

            float desiredLength = child.lengthToParent;
            if (allowStretching) {
                desiredLength *= child.maxStretchFactor;
            }

            Vector2f newChildPos = new Vector2f(joint.position).add(dir.mul(desiredLength));

            // Mass influences stiffness
            float effectiveStiffness = child.stiffness / child.mass;
            effectiveStiffness = Math.min(Math.max(effectiveStiffness, 0f), 1f);

            child.position.lerp(newChildPos, 1.0f - effectiveStiffness);

            // Apply constraint
            applyConstraints(child);

            backward(child, child.position);
        }
    }

    private void applyConstraints(Joint joint) {
        if (joint.parent == null) return;

        Vector2f parentDir = new Vector2f(joint.position).sub(joint.parent.position).normalize();
        Vector2f restDir = new Vector2f(joint.restPosition).sub(joint.parent.restPosition).normalize();

        float angle = (float) Math.toDegrees(Math.atan2(parentDir.y, parentDir.x) - Math.atan2(restDir.y, restDir.x));
        angle = normalizeAngle(angle);

        if (angle < joint.minAngle) {
            rotateAroundParent(joint, joint.minAngle - angle);
        } else if (angle > joint.maxAngle) {
            rotateAroundParent(joint, joint.maxAngle - angle);
        }
    }

    private void rotateAroundParent(Joint joint, float angleDegrees) {
        Vector2f dir = new Vector2f(joint.position).sub(joint.parent.position);
        float length = dir.length();
        double angleRad = Math.toRadians(angleDegrees);

        float cos = (float) Math.cos(angleRad);
        float sin = (float) Math.sin(angleRad);

        float rotatedX = dir.x * cos - dir.y * sin;
        float rotatedY = dir.x * sin + dir.y * cos;

        Vector2f rotatedDir = new Vector2f(rotatedX, rotatedY).normalize().mul(length);
        joint.position.set(joint.parent.position.x + rotatedDir.x, joint.parent.position.y + rotatedDir.y);
    }

    private float normalizeAngle(float angle) {
        while (angle > 180f) angle -= 360f;
        while (angle < -180f) angle += 360f;
        return angle;
    }

    public void resetPose() {
        resetJointPose(root);
    }

    private void resetJointPose(Joint joint) {
        if (joint == null) return;

        joint.position.set(joint.restPosition);
        for (Joint child : joint.children) {
            resetJointPose(child);
        }
    }

    public Joint getRoot() {
        return root;
    }
}
