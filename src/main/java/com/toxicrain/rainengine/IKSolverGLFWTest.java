package com.toxicrain.rainengine;

import com.toxicrain.rainengine.core.render.FabrikRenderer;
import com.toxicrain.rainengine.texture.TextureSystem;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBEasyFont;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class IKSolverGLFWTest {

    private long window;
    private FabrikSolver solver;
    private FabrikSolver.Joint endEffector;
    private float mouseX = 0, mouseY = 0;
    private int width = 800, height = 600;

    public static void main(String[] args) {
        new IKSolverGLFWTest().run();
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(width, height, "IK Solver (GLFW)", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1); // Enable V-Sync
        GL.createCapabilities();

        // Get actual framebuffer size on creation
        int[] fbWidth = new int[1];
        int[] fbHeight = new int[1];
        GLFW.glfwGetFramebufferSize(window, fbWidth, fbHeight);
        width = fbWidth[0];
        height = fbHeight[0];

        // Resize callback
        GLFW.glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            width = w;
            height = h;
        });

        // Center window
        GLFW.glfwSetWindowPos(window, 100, 100);

        // Mouse position callback
        GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            mouseX = (float)xpos;
            mouseY = (float)ypos;
        });

        // Build solver for a more detailed human-like rig with multiple joints
        FabrikSolver.Joint root = new FabrikSolver.Joint(new Vector2f(100, 100), "root");
        FabrikSolver.Joint torso = new FabrikSolver.Joint(new Vector2f(100, 150), "torso");
        FabrikSolver.Joint leftHip = new FabrikSolver.Joint(new Vector2f(50, 200), "left hip");
        FabrikSolver.Joint leftKnee = new FabrikSolver.Joint(new Vector2f(50, 250), "left knee");
        FabrikSolver.Joint leftFoot = new FabrikSolver.Joint(new Vector2f(50, 300), "left foot");
        FabrikSolver.Joint rightHip = new FabrikSolver.Joint(new Vector2f(150, 200), "right hip");
        FabrikSolver.Joint rightKnee = new FabrikSolver.Joint(new Vector2f(150, 250), "right knee");
        FabrikSolver.Joint rightFoot = new FabrikSolver.Joint(new Vector2f(150, 300), "right foot");
        FabrikSolver.Joint leftShoulder = new FabrikSolver.Joint(new Vector2f(50, 100), "left shoulder");
        FabrikSolver.Joint leftElbow = new FabrikSolver.Joint(new Vector2f(50, 50), "left elbow");
        FabrikSolver.Joint leftWrist = new FabrikSolver.Joint(new Vector2f(50, 0), "left wrist");
        FabrikSolver.Joint rightShoulder = new FabrikSolver.Joint(new Vector2f(150, 100), "right shoulder");
        FabrikSolver.Joint rightElbow = new FabrikSolver.Joint(new Vector2f(150, 50), "right elbow");
        FabrikSolver.Joint rightWrist = new FabrikSolver.Joint(new Vector2f(150, 0), "right wrist");
        FabrikSolver.Joint head = new FabrikSolver.Joint(new Vector2f(100, 50), "head");

        // Connect joints (Hierarchy)
        root.addChild(torso);
        torso.addChild(leftHip);
        torso.addChild(rightHip);
        torso.addChild(leftShoulder);
        torso.addChild(rightShoulder);
        torso.addChild(head);

        // Left leg (hip -> knee -> foot)
        leftHip.addChild(leftKnee);
        leftKnee.addChild(leftFoot);

        // Right leg (hip -> knee -> foot)
        rightHip.addChild(rightKnee);
        rightKnee.addChild(rightFoot);

        // Left arm (shoulder -> elbow -> wrist)
        leftShoulder.addChild(leftElbow);
        leftElbow.addChild(leftWrist);

        // Right arm (shoulder -> elbow -> wrist)
        rightShoulder.addChild(rightElbow);
        rightElbow.addChild(rightWrist);

        endEffector = head; // Set the end effector to head

        // Create solver with the root joint
        solver = new FabrikSolver(torso);
        solver.setAllowStretching(true);
        solver.setMaxIterations(25000);
        solver.setTolerance(0.5f);

        // Set joint stiffness for realism
        root.stiffness = 1f;
        torso.stiffness = 0.9f;
        leftHip.stiffness = 0.8f;
        leftKnee.stiffness = 0.7f;
        leftFoot.stiffness = 0.6f;
        rightHip.stiffness = 0.8f;
        rightKnee.stiffness = 0.7f;
        rightFoot.stiffness = 0.6f;
        leftShoulder.stiffness = 0.8f;
        leftElbow.stiffness = 0.7f;
        leftWrist.stiffness = 0.6f;
        rightShoulder.stiffness = 0.8f;
        rightElbow.stiffness = 0.7f;
        rightWrist.stiffness = 0.6f;
        head.stiffness = 0.5f;

        // Set max stretch factor for each limb
        leftHip.maxStretchFactor = 1.5f;
        rightHip.maxStretchFactor = 1.5f;
        leftShoulder.maxStretchFactor = 1.3f;
        rightShoulder.maxStretchFactor = 1.3f;
        leftKnee.maxStretchFactor = 1.2f;
        rightKnee.maxStretchFactor = 1.2f;
        leftFoot.maxStretchFactor = 1.2f;
        rightFoot.maxStretchFactor = 1.2f;
        leftElbow.maxStretchFactor = 1.3f;
        rightElbow.maxStretchFactor = 1.3f;
        leftWrist.maxStretchFactor = 1.3f;
        rightWrist.maxStretchFactor = 1.3f;
        head.maxStretchFactor = 1.2f;
        TextureSystem.initTextures();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Update
            update();

            // Render
            render();

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void update() {

    }

    private void render() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-width/2.0, width/2.0, -height/2.0, height/2.0, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Draw IK chain
        FabrikRenderer.render(solver);

        traverseAndLabel(solver.getRoot());
    }

    private void cleanup() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void traverseAndLabel(FabrikSolver.Joint joint) {
        // Position of the joint in world coords:
        float x = joint.position.x;
        float y = joint.position.y;


        // Render its name just above/right of it
        renderText(joint == endEffector ? "endEffector" : joint.toString(), x + 5, y + 5);

        // Recurse children
        for (FabrikSolver.Joint child : joint.children) {
            traverseAndLabel(child);
        }
    }
    private void renderText(String text, float worldX, float worldY) {
        // 1) Convert your world coords (-w/2..+w/2, -h/2..+h/2) → pixel coords (0..w, 0..h)
        float px = worldX + width * 0.5f;
        float py = height * 0.5f - worldY;

        // 2) Push a 2D pixel projection
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        // (Optional) enable blending if you want antialiased font edges
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // 3) Print into a ByteBuffer
        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(px, py, text, null, charBuffer);

        // 4) Draw them
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);
        glDrawArrays(GL_QUADS, 0, quads * 4);
        glDisableClientState(GL_VERTEX_ARRAY);

        glDisable(GL_BLEND);

        // 5) Pop back to your old projection / modelview
        glPopMatrix();                 // MODELVIEW
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();                 // PROJECTION
        glMatrixMode(GL_MODELVIEW);    // restore to modelview for subsequent draws
    }


}
