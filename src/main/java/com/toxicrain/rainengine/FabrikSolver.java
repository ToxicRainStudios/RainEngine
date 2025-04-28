package com.toxicrain.rainengine;

import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.List;

public class FabrikSolver {

    private List<Vector2f> joints;
    private List<Float> lengths;
    private float totalLength;
    private Vector2f basePosition;

    public FabrikSolver(List<Vector2f> joints) {
        this.joints = joints;
        this.lengths = new ArrayList<>();
        this.totalLength = 0f;
        this.basePosition = new Vector2f(joints.get(0));

        // Calculate bone lengths
        for (int i = 0; i < joints.size() - 1; i++) {
            float len = joints.get(i + 1).distance(joints.get(i));
            lengths.add(len);
            totalLength += len;
        }
    }

    public void solve(Vector2f target) {
        if (target.distance(basePosition) > totalLength) {
            // Stretch towards target
            for (int i = 0; i < joints.size() - 1; i++) {
                Vector2f dir = new Vector2f(target).sub(joints.get(i)).normalize();
                joints.get(i + 1).set(new Vector2f(joints.get(i)).add(dir.mul(lengths.get(i))));
            }
        } else {
            // Forward reaching
            joints.get(joints.size() - 1).set(target);
            for (int i = joints.size() - 2; i >= 0; i--) {
                Vector2f dir = new Vector2f(joints.get(i)).sub(joints.get(i + 1)).normalize();
                joints.get(i).set(new Vector2f(joints.get(i + 1)).add(dir.mul(lengths.get(i))));
            }
            // Backward reaching
            joints.get(0).set(basePosition);
            for (int i = 0; i < joints.size() - 1; i++) {
                Vector2f dir = new Vector2f(joints.get(i + 1)).sub(joints.get(i)).normalize();
                joints.get(i + 1).set(new Vector2f(joints.get(i)).add(dir.mul(lengths.get(i))));
            }
        }
    }

    public List<Vector2f> getJoints() {
        return joints;
    }
}

