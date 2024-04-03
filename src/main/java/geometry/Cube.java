package geometry;

import util.Color;

import static org.lwjgl.opengl.GL11.*;

public class Cube {
    public static void drawCube(Color color, float hightx, float highty, float hightz, float x, float y, float z) {
        // Set the color of the cube
        float red = Color.getRed(color);
        float green = Color.getGreen(color);
        float blue = Color.getBlue(color);
        glColor3f(red,green,blue);

        // Set up transformations
        glPushMatrix();
        glTranslatef(x, y, z);
        glRotatef(0, 1.0f, 1.0f, 1.0f);
        glScalef(1, 1, 1);

        // Draw the cube
        glBegin(GL_QUADS);
        // Front face
        glVertex3f(-1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        glVertex3f(1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        glVertex3f(-1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        // Back face
        glVertex3f(-1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        glVertex3f(-1.0f* hightx, 1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        // Top face
        glVertex3f(-1.0f * hightx, 1.0f * highty, -1.0f * hightz);
        glVertex3f(-1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, -1.0f * hightz);
        // Bottom face
        glVertex3f(-1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        glVertex3f(-1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        // Right face
        glVertex3f(1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, -1.0f * hightz);
        glVertex3f(1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        glVertex3f(1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        // Left face
        glVertex3f(-1.0f * hightx, -1.0f * highty, -1.0f * hightz);
        glVertex3f(-1.0f * hightx, -1.0f * highty, 1.0f * hightz);
        glVertex3f(-1.0f * hightx, 1.0f * highty, 1.0f * hightz);
        glVertex3f(-1.0f * hightx, 1.0f * highty, -1.0f * hightz);
        glEnd();

        // Restore the previous matrix
        glPopMatrix();

    }
}