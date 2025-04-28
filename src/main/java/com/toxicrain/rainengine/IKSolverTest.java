package com.toxicrain.rainengine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.joml.Vector2f;

public class IKSolverTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("IK Solver (Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            IKSolverPanel panel = new IKSolverPanel();
            frame.setContentPane(panel);
            frame.setVisible(true);
            panel.start();
        });
    }
}

class IKSolverPanel extends JPanel implements MouseMotionListener, ActionListener {
    private final FabrikSolver solver;
    private final Timer timer;
    private Point mouse = new Point(400, 300);
    private FabrikSolver.Joint endEffector;

    public IKSolverPanel() {
        setBackground(Color.BLACK);

        // Create joints manually
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
        solver.setAllowStretching(true); // allow bones to stretch
        solver.setMaxIterations(15);
        solver.setTolerance(0.5f); // bit more tolerance for faster solving

        // Configure joints
        root.stiffness = 1f;
        joint1.stiffness = 0.99f;
        joint2.stiffness = 0.99f;
        joint3.stiffness = 0.99f;
        joint4.stiffness = 0.3f; // end effector is very loose

        joint1.maxStretchFactor = 2f; // 20% stretch allowed
        joint2.maxStretchFactor = 1f;
        joint3.maxStretchFactor = 1f;
        joint4.maxStretchFactor = 1.3f; // end can stretch a lot

        addMouseMotionListener(this);

        timer = new Timer(1000 / 60, this); // 60 FPS
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        g2.translate(w / 2, h / 2);
        g2.setStroke(new BasicStroke(3));

        // Draw recursively
        drawJoint(g2, solver.getRoot());
    }

    private void drawJoint(Graphics2D g2, FabrikSolver.Joint joint) {
        int x = Math.round(joint.position.x), y = -Math.round(joint.position.y);

        // Draw bone connections
        for (FabrikSolver.Joint child : joint.children) {
            int cx = Math.round(child.position.x), cy = -Math.round(child.position.y);

            // Color based on stretch amount
            float currentLength = joint.position.distance(child.position);
            float originalLength = child.lengthToParent;
            if (currentLength > originalLength * 1.01f) {
                g2.setColor(Color.ORANGE); // stretching
            } else {
                g2.setColor(Color.RED); // normal
            }
            g2.drawLine(x, y, cx, cy);

            drawJoint(g2, child); // Recurse
        }

        // Draw joint points
        g2.setColor(Color.CYAN);
        g2.fillOval(x - 5, y - 5, 10, 10);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse = e.getPoint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Convert mouse to world coords (center origin, Y up)
        float worldX = mouse.x - getWidth() * 0.5f;
        float worldY = getHeight() * 0.5f - mouse.y;
        solver.solve(endEffector, new Vector2f(worldX, worldY));
        repaint();
    }
}
