package com.toxicrain.rainengine.core.editor;

import com.toxicrain.rainengine.core.Constants;
import com.toxicrain.rainengine.core.datatypes.vector.Vector2;
import com.toxicrain.rainengine.core.json.MapInfoParser;
import com.toxicrain.rainengine.light.LightSystem;
import com.toxicrain.rainengine.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class MapEditorMenu extends JFrame {

    private JTextField mapNameField, widthField, heightField, spawnXField, spawnYField;
    private JTable lightTable;
    private LightTableModel lightTableModel;
    private JTextArea sliceViewer;
    private DefaultListModel<String> subMapListModel;
    private JPanel lightPreviewPanel;

    private String currentMapName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MapEditorMenu::new);
    }

    public MapEditorMenu() {
        setTitle("Map Editor");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        add(mainPanel);

        // === Map Info Panel ===
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Map Info"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        mapNameField = new JTextField(12);
        widthField = new JTextField(6);
        heightField = new JTextField(6);
        spawnXField = new JTextField(6);
        spawnYField = new JTextField(6);

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Map Name:"), gbc);
        gbc.gridx = 1; infoPanel.add(mapNameField, gbc);
        JButton loadButton = new JButton("Load Map");
        gbc.gridx = 2; infoPanel.add(loadButton, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Map Width:"), gbc);
        gbc.gridx = 1; infoPanel.add(widthField, gbc);
        gbc.gridx = 2; infoPanel.add(new JLabel("Map Height:"), gbc);
        gbc.gridx = 3; infoPanel.add(heightField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; infoPanel.add(new JLabel("Spawn X:"), gbc);
        gbc.gridx = 1; infoPanel.add(spawnXField, gbc);
        gbc.gridx = 2; infoPanel.add(new JLabel("Spawn Y:"), gbc);
        gbc.gridx = 3; infoPanel.add(spawnYField, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // === Tabbed Pane ===
        JTabbedPane tabs = new JTabbedPane();

        // === Light Table Tab ===
        lightTableModel = new LightTableModel();
        lightTable = new JTable(lightTableModel);
        JScrollPane tableScroll = new JScrollPane(lightTable);

        JPanel lightButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addLightBtn = new JButton("Add Light");
        JButton removeLightBtn = new JButton("Remove Selected");
        lightButtonPanel.add(addLightBtn);
        lightButtonPanel.add(removeLightBtn);

        JPanel lightTablePanel = new JPanel(new BorderLayout(5, 5));
        lightTablePanel.add(tableScroll, BorderLayout.CENTER);
        lightTablePanel.add(lightButtonPanel, BorderLayout.SOUTH);

        tabs.addTab("Lights", lightTablePanel);


        lightPreviewPanel = new JPanel() {
            private float zoom = 1.0f;
            private final int padding = 20;

            {
                // Enable mouse wheel zoom
                addMouseWheelListener(e -> {
                    int rotation = e.getWheelRotation();
                    zoom *= (rotation > 0) ? 0.9f : 1.1f;
                    zoom = Math.max(0.2f, Math.min(5.0f, zoom));
                    repaint();
                });

                // Tooltip on hover
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        String tooltip = null;
                        try {
                            int width = Integer.parseInt(widthField.getText().trim());
                            int height = Integer.parseInt(heightField.getText().trim());
                            int cellSize = Math.min((int)((getWidth() - 2*padding) / Math.max(width, 1) * zoom),
                                    (int)((getHeight() - 2*padding) / Math.max(height, 1) * zoom));

                            for (float[] light : LightSystem.getLightSources()) {
                                int px = padding + (int)(light[0] * cellSize);
                                int py = padding + (int)(light[1] * cellSize);
                                int radius = cellSize / 3;

                                int dx = e.getX() - px;
                                int dy = e.getY() - py;
                                if (dx*dx + dy*dy <= radius*radius) {
                                    tooltip = String.format("Light at (%.2f, %.2f) strength %.2f", light[0], light[1], light[2]);
                                    break;
                                }
                            }
                        } catch (NumberFormatException ignored) {}

                        setToolTipText(tooltip);
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g.create();

                // Anti-aliasing for smoother rendering
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                try {
                    int width = Integer.parseInt(widthField.getText().trim());
                    int height = Integer.parseInt(heightField.getText().trim());

                    int cellSizeX = (int) ((getWidth() - 2 * padding) / Math.max(width, 1) * zoom);
                    int cellSizeY = (int) ((getHeight() - 2 * padding) / Math.max(height, 1) * zoom);
                    int cellSize = Math.min(cellSizeX, cellSizeY);

                    // Background fill
                    g2d.setColor(new Color(30, 30, 30));
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Draw grid lines with subtle color
                    g2d.setColor(new Color(100, 100, 100));
                    for (int x = 0; x <= width; x++) {
                        int px = padding + x * cellSize;
                        g2d.drawLine(px, padding, px, padding + height * cellSize);
                    }
                    for (int y = 0; y <= height; y++) {
                        int py = padding + y * cellSize;
                        g2d.drawLine(padding, py, padding + width * cellSize, py);
                    }

                    // Draw spawn point
                    try {
                        int spawnX = Integer.parseInt(spawnXField.getText().trim());
                        int spawnY = Integer.parseInt(spawnYField.getText().trim());
                        g2d.setColor(new Color(0, 255, 0, 180));
                        int sx = padding + (int)(spawnX * cellSize);
                        int sy = padding + (int)(spawnY * cellSize);
                        int spawnRadius = cellSize / 2;
                        g2d.fillOval(sx - spawnRadius / 2, sy - spawnRadius / 2, spawnRadius, spawnRadius);
                    } catch (NumberFormatException ignored) {}

                    // Draw lights with glow effect
                    for (float[] light : LightSystem.getLightSources()) {
                        float lx = light[0];
                        float ly = light[1];
                        float strength = light[2];

                        int px = padding + (int)(lx * cellSize);
                        int py = padding + (int)(ly * cellSize);

                        int radius = cellSize / 3;

                        // Draw glow: a translucent yellow circle scaling with strength
                        int glowRadius = (int) (radius * 2 * strength);
                        GradientPaint glow = new GradientPaint(
                                px, py, new Color(255, 255, 0, 150),
                                px + glowRadius, py + glowRadius, new Color(255, 255, 0, 0), true);
                        g2d.setPaint(glow);
                        g2d.fillOval(px - glowRadius / 2, py - glowRadius / 2, glowRadius, glowRadius);

                        // Draw solid circle in center
                        g2d.setColor(Color.YELLOW);
                        g2d.fillOval(px - radius / 2, py - radius / 2, radius, radius);

                        // Draw outline
                        g2d.setColor(Color.ORANGE.darker());
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawOval(px - radius / 2, py - radius / 2, radius, radius);

                        // Draw strength text
                        g2d.setColor(Color.BLACK);
                        String text = String.format("%.2f", strength);
                        FontMetrics fm = g2d.getFontMetrics();
                        int textWidth = fm.stringWidth(text);
                        int textHeight = fm.getAscent();
                        g2d.drawString(text, px - textWidth / 2, py + textHeight / 2);
                    }

                } catch (NumberFormatException ignored) {
                    // ignore if invalid input
                }

                g2d.dispose();
            }
        };
        lightPreviewPanel.setPreferredSize(new Dimension(400, 400));
        tabs.addTab("Preview", lightPreviewPanel);

        // === Slices Tab ===
        sliceViewer = new JTextArea();
        sliceViewer.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        sliceViewer.setEditable(false);
        tabs.addTab("Slices", new JScrollPane(sliceViewer));

        // === Submaps Tab ===
        subMapListModel = new DefaultListModel<>();
        JList<String> subMapList = new JList<>(subMapListModel);
        tabs.addTab("Submaps", new JScrollPane(subMapList));

        mainPanel.add(tabs, BorderLayout.CENTER);

        // === Save Button ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        bottomPanel.add(saveButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // === Action Listeners ===
        loadButton.addActionListener(this::onLoadClicked);
        saveButton.addActionListener(this::onSaveClicked);

        addLightBtn.addActionListener(e -> {
            lightTableModel.addLight(0f, 0f, 1f);
            lightPreviewPanel.repaint();
        });

        removeLightBtn.addActionListener(e -> {
            int selected = lightTable.getSelectedRow();
            if (selected >= 0) {
                lightTableModel.removeLight(selected);
                lightPreviewPanel.repaint();
            }
        });

        setVisible(true);
    }

    private void onLoadClicked(ActionEvent e) {
        currentMapName = mapNameField.getText().trim();
        try {
            LightSystem.getLightSources().clear(); // Clear before reload
            MapInfoParser.getInstance().parseMapFile(currentMapName);

            Vector2 size = MapInfoParser.getInstance().mapSize;
            Vector2 spawn = MapInfoParser.getInstance().playerSpawnPos;

            widthField.setText(String.valueOf((int) size.x));
            heightField.setText(String.valueOf((int) size.y));
            spawnXField.setText(String.valueOf((int) spawn.x));
            spawnYField.setText(String.valueOf((int) spawn.y));

            // Read JSON data
            JSONArray topArray = MapInfoParser.getInstance().getMapJson();
            JSONObject mainMap = topArray.getJSONObject(0);

            // Update slice viewer
            JSONArray slices = mainMap.getJSONArray("slices").getJSONArray(0);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < slices.length(); i++) {
                sb.append(slices.getString(i)).append("\n");
            }
            sliceViewer.setText(sb.toString());

            // Update submaps
            subMapListModel.clear();
            JSONArray subMaps = mainMap.optJSONArray("subMaps");
            if (subMaps != null) {
                for (int i = 0; i < subMaps.length(); i++) {
                    JSONObject sm = subMaps.getJSONObject(i);
                    subMapListModel.addElement(sm.getString("name") + " (" + sm.getInt("offsetX") + "," + sm.getInt("offsetY") + ")");
                }
            }

            lightTableModel.fireTableDataChanged();
            lightPreviewPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load map: " + ex.getMessage());
        }
    }

    private void onSaveClicked(ActionEvent e) {
        if (currentMapName == null) return;

        try {
            String file = FileUtils.getCurrentWorkingDirectory(
                    Constants.FileConstants.MAP_PATH + currentMapName + ".json");
            String jsonStr = FileUtils.readFile(file);
            JSONArray jsonArray = new JSONArray(jsonStr);

            JSONObject mainPart = jsonArray.getJSONObject(0);

            mainPart.put("xsize", Integer.parseInt(widthField.getText().trim()));
            mainPart.put("ysize", Integer.parseInt(heightField.getText().trim()));
            mainPart.put("playerx", Integer.parseInt(spawnXField.getText().trim()));
            mainPart.put("playery", Integer.parseInt(spawnYField.getText().trim()));

            JSONArray lightingArray = new JSONArray();
            for (float[] light : LightSystem.getLightSources()) {
                JSONObject obj = new JSONObject();
                obj.put("x", light[0]);
                obj.put("y", light[1]);
                obj.put("strength", light[2]);
                lightingArray.put(obj);
            }

            mainPart.put("lighting", lightingArray);

            FileUtils.writeFile(file, jsonArray.toString(4));
            JOptionPane.showMessageDialog(this, "Map saved successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving map: " + ex.getMessage());
        }
    }

    // === Light Table Model ===
    static class LightTableModel extends AbstractTableModel {
        private final String[] columns = {"X", "Y", "Strength"};
        private final List<float[]> data = LightSystem.getLightSources();

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public boolean isCellEditable(int row, int col) { return true; }

        @Override
        public Object getValueAt(int row, int col) {
            return String.format("%.2f", data.get(row)[col]);
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            try {
                data.get(row)[col] = Float.parseFloat(value.toString());
                fireTableCellUpdated(row, col);
            } catch (NumberFormatException ignored) {}
        }

        public void addLight(float x, float y, float strength) {
            LightSystem.addLightSource(x, y, strength);
            fireTableDataChanged();
        }

        public void removeLight(int row) {
            float[] light = data.get(row);
            LightSystem.removeLightSource(light[0], light[1], light[2]);
            fireTableDataChanged();
        }
    }
}
