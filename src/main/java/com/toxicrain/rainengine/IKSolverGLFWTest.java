package com.toxicrain.rainengine;

import com.toxicrain.rainengine.core.render.FabrikRenderer;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
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

        // Center window on screen
        GLFW.glfwSetWindowPos(window, 100, 100);

        // Mouse position callback
        GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            mouseX = (float)xpos;
            mouseY = (float)ypos;
        });

        // Build solver (same as your Swing version)
        FabrikSolver.Joint root = new FabrikSolver.Joint(new Vector2f(50, 50));
        FabrikSolver.Joint joint1 = new FabrikSolver.Joint(new Vector2f(150, 50));
        FabrikSolver.Joint joint2 = new FabrikSolver.Joint(new Vector2f(200, 50));
        FabrikSolver.Joint joint3 = new FabrikSolver.Joint(new Vector2f(250, 50));
        FabrikSolver.Joint joint4 = new FabrikSolver.Joint(new Vector2f(300, 50));

        root.addChild(joint1);
        joint1.addChild(joint2);
        joint2.addChild(joint3);
        joint3.addChild(joint4);

        endEffector = joint4;

        solver = new FabrikSolver(root);
        solver.setAllowStretching(true);
        solver.setMaxIterations(15);
        solver.setTolerance(0.5f);

        // Joint settings
        root.stiffness = 1f;
        joint1.stiffness = 0.99f;
        joint2.stiffness = 0.99f;
        joint3.stiffness = 0.99f;
        joint4.stiffness = 0.3f;

        joint1.maxStretchFactor = 2f;
        joint2.maxStretchFactor = 1f;
        joint3.maxStretchFactor = 1f;
        joint4.maxStretchFactor = 1.3f;
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
        // Convert mouse to world coordinates
        float worldX = mouseX - width / 2.0f;
        float worldY = height / 2.0f - mouseY;
        solver.solve(endEffector, new Vector2f(worldX, worldY));
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
    }

    private void cleanup() {
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}

