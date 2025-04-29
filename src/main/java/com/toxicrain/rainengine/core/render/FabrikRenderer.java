package com.toxicrain.rainengine.core.render;

import com.toxicrain.rainengine.FabrikSolver;
import com.toxicrain.rainengine.texture.TextureInfo;
import com.toxicrain.rainengine.texture.TextureSystem;
import org.joml.Vector2f;
import static org.lwjgl.opengl.GL11.*;

public class FabrikRenderer {

    // Name of the textures to use
    private static final String JOINT_TEXTURE_NAME = "joint"; // Put "joint.png" in /resources/images
    private static final String LINE_TEXTURE_NAME = "line";   // Put "line.png" in /resources/images

    public static void render(FabrikSolver solver) {
        if (solver == null || solver.getRoot() == null) return;

        glPushMatrix();
        {
            glEnable(GL_TEXTURE_2D);

            TextureInfo jointTexture = TextureSystem.getTexture(JOINT_TEXTURE_NAME);
            TextureInfo lineTexture = TextureSystem.getTexture(LINE_TEXTURE_NAME);

            // Render lines first (connections)
            if (lineTexture != null) {
                glBindTexture(GL_TEXTURE_2D, lineTexture.textureId);
            } else {
                glBindTexture(GL_TEXTURE_2D, 0);
            }
            renderTexturedLines(solver.getRoot(), 0, lineTexture != null);

            // Render joints (points)
            if (jointTexture != null) {
                glBindTexture(GL_TEXTURE_2D, jointTexture.textureId);
            } else {
                glBindTexture(GL_TEXTURE_2D, 0);
            }
            renderTexturedPoints(solver.getRoot(), 0, jointTexture != null);

            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    private static void renderTexturedLines(FabrikSolver.Joint joint, int depth, boolean textured) {
        for (FabrikSolver.Joint child : joint.children) {
            if (textured) {
                renderLineTexture(joint.position, child.position);
            } else {
                glColor3f(1.0f, 1.0f, 1.0f);
                glLineWidth(2.0f);
                glBegin(GL_LINES);
                glVertex2f(joint.position.x, joint.position.y);
                glVertex2f(child.position.x, child.position.y);
                glEnd();
            }
            renderTexturedLines(child, depth + 1, textured);
        }
    }

    private static void renderTexturedPoints(FabrikSolver.Joint joint, int depth, boolean textured) {
        if (textured) {
            renderJointTexture(joint.position);
        } else {
            glColor3f(1.0f, 0.0f, 0.0f); // Default fallback color
            glPointSize(8.0f);
            glBegin(GL_POINTS);
            glVertex2f(joint.position.x, joint.position.y);
            glEnd();
        }

        for (FabrikSolver.Joint child : joint.children) {
            renderTexturedPoints(child, depth + 1, textured);
        }
    }

    private static void renderJointTexture(Vector2f position) {
        float size = 16.0f; // Size of the joint image
        float halfSize = size / 2.0f;

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(position.x - halfSize, position.y - halfSize);
        glTexCoord2f(1, 0); glVertex2f(position.x + halfSize, position.y - halfSize);
        glTexCoord2f(1, 1); glVertex2f(position.x + halfSize, position.y + halfSize);
        glTexCoord2f(0, 1); glVertex2f(position.x - halfSize, position.y + halfSize);
        glEnd();
    }

    private static void renderLineTexture(Vector2f start, Vector2f end) {
        float thickness = 8.0f; // Thickness of the line image
        Vector2f dir = new Vector2f(end).sub(start);
        float length = dir.length();
        dir.normalize();

        Vector2f perp = new Vector2f(-dir.y, dir.x).mul(thickness / 2.0f);

        Vector2f v0 = new Vector2f(start).add(perp);
        Vector2f v1 = new Vector2f(start).sub(perp);
        Vector2f v2 = new Vector2f(end).sub(perp);
        Vector2f v3 = new Vector2f(end).add(perp);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(v0.x, v0.y);
        glTexCoord2f(1, 0); glVertex2f(v1.x, v1.y);
        glTexCoord2f(1, 1); glVertex2f(v2.x, v2.y);
        glTexCoord2f(0, 1); glVertex2f(v3.x, v3.y);
        glEnd();
    }
}
