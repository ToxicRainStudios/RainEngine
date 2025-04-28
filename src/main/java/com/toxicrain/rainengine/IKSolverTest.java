package com.toxicrain.rainengine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
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

    public IKSolverPanel() {
        setBackground(Color.BLACK);
        // Initial joints
        solver = new FabrikSolver(Arrays.asList(
                new Vector2f(0, 0),
                new Vector2f(150, 50),
                new Vector2f(250, 50)
        ));
        addMouseMotionListener(this);
        // 60 FPS repaint
        timer = new Timer(1000 / 60, this);
    }

    public void start() {
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Setup 2D center origin
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        g2.translate(w / 2, h / 2);
        g2.setStroke(new BasicStroke(3));

        // Draw bones
        g2.setColor(Color.RED);
        for (int i = 0; i < solver.getJoints().size() - 1; i++) {
            Vector2f a = solver.getJoints().get(i);
            Vector2f b = solver.getJoints().get(i + 1);
            g2.drawLine(
                    Math.round(a.x), -Math.round(a.y),
                    Math.round(b.x), -Math.round(b.y)
            );
        }

        // Draw joints
        g2.setColor(Color.CYAN);
        for (Vector2f joint : solver.getJoints()) {
            int x = Math.round(joint.x), y = -Math.round(joint.y);
            g2.fillOval(x - 5, y - 5, 10, 10);
        }
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
        solver.solve(new Vector2f(worldX, worldY));
        repaint();
    }
}
