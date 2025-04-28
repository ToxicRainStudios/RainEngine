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

        public Joint(Vector2f position) {
            this.position = new Vector2f(position);
            this.restPosition = new Vector2f(position);
        }

        public void addChild(Joint child) {
            child.parent = this;
            child.lengthToParent = child.position.distance(this.position);
            children.add(child);
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
        // Move end effector to target
        joint.position.set(target);

        if (joint.parent != null) {
            Vector2f dir = new Vector2f(joint.parent.position).sub(joint.position);
            float distance = dir.length();
            dir.normalize();

            // Determine target length
            float desiredLength = joint.lengthToParent;
            if (allowStretching) {
                desiredLength *= joint.maxStretchFactor;
            }

            Vector2f newParentPos = new Vector2f(joint.position).add(dir.mul(desiredLength));

            // Stiffness interpolation
            joint.parent.position.lerp(newParentPos, 1.0f - joint.parent.stiffness);

            forward(joint.parent, joint.parent.position);
        }
    }

    private void backward(Joint joint, Vector2f position) {
        // Move joint to position
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

            // Stiffness interpolation
            child.position.lerp(newChildPos, 1.0f - child.stiffness);

            backward(child, child.position);
        }
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
