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
            glColor3f(1.0f, 1.0f, 1.0f); // White lines
            glBegin(GL_LINES);
            renderLines(solver.getRoot());
            glEnd();

            glPointSize(8.0f);
            glBegin(GL_POINTS);
            renderPoints(solver.getRoot());
            glEnd();
        }
        glPopMatrix();
    }

    private static void renderLines(FabrikSolver.Joint joint) {
        for (FabrikSolver.Joint child : joint.children) {
            glVertex2f(joint.position.x, joint.position.y);
            glVertex2f(child.position.x, child.position.y);
            renderLines(child); // Recursive
        }
    }

    private static void renderPoints(FabrikSolver.Joint joint) {
        glVertex2f(joint.position.x, joint.position.y);
        for (FabrikSolver.Joint child : joint.children) {
            renderPoints(child); // Recursive
        }
    }
}

