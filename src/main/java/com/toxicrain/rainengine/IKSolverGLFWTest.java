package com.toxicrain.rainengine;

import com.github.strubium.windowmanager.imgui.GuiManager;
import com.github.strubium.windowmanager.imgui.ImguiHandler;
import com.github.strubium.windowmanager.window.WindowManager;
import com.toxicrain.rainengine.core.eventbus.events.render.RenderGuiEvent;
import com.toxicrain.rainengine.core.render.FabrikRenderer;
import com.toxicrain.rainengine.factories.GameFactory;
import com.toxicrain.rainengine.gui.GuiReg;
import com.toxicrain.rainengine.gui.SolverGuiReg;
import com.toxicrain.rainengine.texture.TextureSystem;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBEasyFont;

import java.nio.ByteBuffer;

import static com.toxicrain.rainengine.core.GameEngine.windowManager;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class IKSolverGLFWTest {

    public static WindowManager windowManager;
    public static ImguiHandler imguiApp;
    public static GuiManager guiManager;
    public static SolverGuiReg guiReg;

    private FabrikSolver solver;
    private FabrikSolver.Joint endEffector;
    private float mouseX = 0, mouseY = 0;
    private int width = 800, height = 600;

    public static void main(String[] args) {
        windowManager = new WindowManager(800, 600, true);
        windowManager.createWindow("IK Solver (GLFW)", true);
        imguiApp = new ImguiHandler(windowManager);
        imguiApp.initialize("#version 130");
        guiManager = new GuiManager();
        guiReg = new SolverGuiReg();
        guiManager.registerGUI("Debug", (v) -> guiReg.drawDebugInfo());
        guiManager.addActiveGUI("Debug");


        new IKSolverGLFWTest().run();
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        long window = windowManager.window;
        GL.createCapabilities();

        // 1) Set your clear color once
        glClearColor(0f, 0f, 0f, 1f);

        // 2) Disable depth test since you're doing pure 2D text + lines
        glDisable(GL_DEPTH_TEST);

        // Get actual framebuffer size
        int[] fbW = new int[1], fbH = new int[1];
        glfwGetFramebufferSize(window, fbW, fbH);
        width = fbW[0];
        height = fbH[0];

        glfwSetFramebufferSizeCallback(window, (win, w, h) -> {
            width = w;
            height = h;
        });
        glfwSetCursorPosCallback(window, (win, x, y) -> {
            mouseX = (float) x;
            mouseY = (float) y;
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
        // … set stiffness & stretch …

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
        while (!windowManager.shouldClose()) {
            update();
            render();
            windowManager.swapAndPoll();
        }
    }

    private void update() {
        // your IK target / mouse → solver update
        solver.solve(endEffector, new Vector2f(mouseX - width * 0.5f, height * 0.5f - mouseY));
    }

    private void render() {
        // 3) Clear both color AND depth buffers
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set up orthographic projection
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-width / 2.0, width / 2.0, -height / 2.0, height / 2.0, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Draw your chain
        FabrikRenderer.render(solver);
        traverseAndLabel(solver.getRoot());

        imguiApp.handleInput(windowManager.window);
        imguiApp.newFrame();
        guiManager.render();

        imguiApp.render();
    }

    private void cleanup() {
        windowManager.destroy();
    }

    private void traverseAndLabel(FabrikSolver.Joint joint) {
        float x = joint.position.x, y = joint.position.y;
        renderText(joint == endEffector ? "endEffector" : joint.toString(), x + 5, y + 5);
        for (FabrikSolver.Joint c : joint.children)
            traverseAndLabel(c);
    }

    private void renderText(String text, float worldX, float worldY) {
        // Convert to pixel coords
        float px = worldX + width * 0.5f;
        float py = height * 0.5f - worldY;

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ByteBuffer buf = BufferUtils.createByteBuffer(text.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(px, py, text, null, buf);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, buf);
        glDrawArrays(GL_QUADS, 0, quads * 4);
        glDisableClientState(GL_VERTEX_ARRAY);

        glDisable(GL_BLEND);

        glPopMatrix();                        // MODELVIEW
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();                        // PROJECTION
        glMatrixMode(GL_MODELVIEW);
    }
}
