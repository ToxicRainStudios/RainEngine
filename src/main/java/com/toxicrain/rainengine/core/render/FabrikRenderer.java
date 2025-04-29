package com.toxicrain.rainengine.core.render;

import com.toxicrain.rainengine.FabrikSolver;
import org.joml.Vector2f;
import static org.lwjgl.opengl.GL11.*;

public class FabrikRenderer {

    public static void render(FabrikSolver solver) {
        if (solver == null || solver.getRoot() == null) return;

        glPushMatrix();
        {
            glLineWidth(2.0f);
            glColor3f(1.0f, 1.0f, 1.0f); // White lines for connections
            glBegin(GL_LINES);
            renderLines(solver.getRoot(), 0); // Start from depth 0
            glEnd();

            glPointSize(8.0f);
            glBegin(GL_POINTS);
            renderPoints(solver.getRoot(), 0); // Start from depth 0
            glEnd();
        }
        glPopMatrix();
    }

    private static void renderLines(FabrikSolver.Joint joint, int depth) {
        // Calculate color based on depth, darker red as depth increases
        float red = Math.max(0.0f, 1.0f - 0.1f * depth); // Decrease red intensity with depth
        glColor3f(red, 0.0f, 0.0f); // Red color based on depth

        for (FabrikSolver.Joint child : joint.children) {
            glVertex2f(joint.position.x, joint.position.y);
            glVertex2f(child.position.x, child.position.y);
            renderLines(child, depth + 1); // Recurse to render child joints
        }
    }

    private static void renderPoints(FabrikSolver.Joint joint, int depth) {
        // Calculate color based on depth, darker red as depth increases
        float red = Math.max(0.0f, 1.0f - 0.13f * depth); // Decrease red intensity with depth
        glColor3f(red, 0.0f, 0.0f); // Red color based on depth

        glVertex2f(joint.position.x, joint.position.y);

        for (FabrikSolver.Joint child : joint.children) {
            renderPoints(child, depth + 1); // Recurse to render child points
        }
    }
}
