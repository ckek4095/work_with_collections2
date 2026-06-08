package org.example.gui;

import org.example.models.LabWork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class VisualizationPanel extends JPanel {
    private List<LabWork> labWorks = new ArrayList<>();
    private final Map<Integer, Color> ownerColors = new HashMap<>();
    private Consumer<LabWork> clickHandler;

    public VisualizationPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Визуализация объектов"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LabWork clicked = findClickedObject(e.getX(), e.getY());
                if (clicked != null && clickHandler != null) {
                    clickHandler.accept(clicked);
                }
            }
        });
    }

    public void setLabWorks(List<LabWork> labWorks) {
        this.labWorks = labWorks == null ? new ArrayList<>() : new ArrayList<>(labWorks);
        repaint();
    }

    public void setClickHandler(Consumer<LabWork> clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawCoordinatePlane(g);
        drawObjects(g);
        drawLegend(g);
    }

    private void drawCoordinatePlane(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();

        int centerX = w / 2;
        int centerY = h / 2;

        g2.setColor(new Color(230, 230, 230));

        for (int x = centerX; x < w; x += 50) {
            g2.drawLine(x, 35, x, h - 35);
        }
        for (int x = centerX; x > 0; x -= 50) {
            g2.drawLine(x, 35, x, h - 35);
        }
        for (int y = centerY; y < h; y += 50) {
            g2.drawLine(35, y, w - 35, y);
        }
        for (int y = centerY; y > 0; y -= 50) {
            g2.drawLine(35, y, w - 35, y);
        }

        g2.setColor(new Color(80, 80, 80));
        g2.drawLine(35, centerY, w - 35, centerY);
        g2.drawLine(centerX, 35, centerX, h - 35);

        g2.drawString("X", w - 45, centerY - 8);
        g2.drawString("Y", centerX + 8, 45);
    }

    private void drawObjects(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        for (LabWork labWork : labWorks) {
            if (labWork.getCoordinates() == null) {
                continue;
            }

            int screenX = toScreenX(labWork.getCoordinates().getX());
            int screenY = toScreenY(labWork.getCoordinates().getY());

            int size = calculateSize(labWork);
            Color color = getColorForOwner(labWork.getOwnerId());

            g2.setColor(color);
            g2.fillOval(screenX - size / 2, screenY - size / 2, size, size);

            g2.setColor(color.darker());
            g2.drawOval(screenX - size / 2, screenY - size / 2, size, size);
        }
    }

    private void drawLegend(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int x = getWidth() - 150;
        int y = 40;

        g2.setColor(Color.WHITE);
        g2.fillRect(x - 10, y - 20, 130, 25 + ownerColors.size() * 24);

        g2.setColor(new Color(210, 210, 210));
        g2.drawRect(x - 10, y - 20, 130, 25 + ownerColors.size() * 24);

        g2.setColor(Color.BLACK);
        g2.drawString("Владельцы (ID)", x, y);

        int index = 1;
        for (Map.Entry<Integer, Color> entry : ownerColors.entrySet()) {
            g2.setColor(entry.getValue());
            g2.fillRect(x, y + index * 22 - 10, 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(entry.getKey()), x + 22, y + index * 22);

            index++;
        }
    }

    private int toScreenX(int x) {
        return getWidth() / 2 + x * 4;
    }

    private int toScreenY(Integer y) {
        int safeY = y == null ? 0 : y;
        return getHeight() / 2 - safeY * 4;
    }

    private int calculateSize(LabWork labWork) {
        float minimalPoint = labWork.getMinimalPoint() == null ? 10F : labWork.getMinimalPoint();
        int size = Math.round(12 + minimalPoint / 5);
        return Math.max(14, Math.min(size, 45));
    }

    private Color getColorForOwner(Integer ownerId) {
        int id = ownerId == null ? 0 : ownerId;

        return ownerColors.computeIfAbsent(id, value -> {
            Color[] colors = {
                    new Color(230, 70, 85),
                    new Color(70, 120, 230),
                    new Color(80, 160, 80),
                    new Color(70, 180, 190),
                    new Color(150, 90, 210),
                    new Color(230, 160, 50)
            };
            return colors[Math.abs(value) % colors.length];
        });
    }

    private LabWork findClickedObject(int mouseX, int mouseY) {
        for (LabWork labWork : labWorks) {
            if (labWork.getCoordinates() == null) {
                continue;
            }

            int screenX = toScreenX(labWork.getCoordinates().getX());
            int screenY = toScreenY(labWork.getCoordinates().getY());
            int size = calculateSize(labWork);

            double distance = Math.sqrt(
                    Math.pow(mouseX - screenX, 2) + Math.pow(mouseY - screenY, 2)
            );

            if (distance <= size / 2.0) {
                return labWork;
            }
        }

        return null;
    }
}