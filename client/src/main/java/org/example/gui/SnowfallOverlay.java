package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SnowfallOverlay extends JPanel implements Runnable {
    private ArrayList<Snowflake> snowflakes;
    private Random random;
    private boolean running = true;
    private int width;
    private int height;
    private static final int SNOWFLAKE_COUNT = 150;

    public SnowfallOverlay(int width, int height) {
        this.width = width;
        this.height = height;
        setOpaque(false); // Делаем прозрачным
        setPreferredSize(new Dimension(width, height));

        random = new Random();
        snowflakes = new ArrayList<>();

        for (int i = 0; i < SNOWFLAKE_COUNT; i++) {
            snowflakes.add(new Snowflake());
        }

        Thread animationThread = new Thread(this);
        animationThread.setDaemon(true); // Поток-демон, чтобы не мешать закрытию приложения
        animationThread.start();
    }

    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        revalidate();
    }

    public void stopSnowfall() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            updateSnowflakes();
            repaint();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void updateSnowflakes() {
        for (Snowflake snowflake : snowflakes) {
            snowflake.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Не вызываем super.paintComponent() для прозрачности
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Snowflake snowflake : snowflakes) {
            snowflake.draw(g2d);
        }
    }

    // Класс для отдельной снежинки
    class Snowflake {
        private float x, y;
        private float speed;
        private int size;
        private float wind;

        public Snowflake() {
            reset();
        }

        public void reset() {
            x = random.nextFloat() * width;
            y = random.nextFloat() * height - height;
            speed = 1 + random.nextFloat() * 3;
            size = 2 + random.nextInt(4);
            wind = -0.5f + random.nextFloat() * 1f;
        }

        public void update() {
            y += speed;
            x += wind;

            if (y > height + 10) {
                reset();
                y = -10;
            }

            if (x > width + 10) {
                x = -10;
            } else if (x < -10) {
                x = width + 10;
            }
        }

        public void draw(Graphics2D g2d) {
            float alpha = 0.6f + (speed / 10f);
            alpha = Math.min(alpha, 0.9f);

            g2d.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
            g2d.fillOval((int) x, (int) y, size, size);

            // Добавляем свечение для некоторых снежинок
            if (size > 3 && random.nextInt(100) < 20) {
                g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 0.3f));
                g2d.fillOval((int) x - 1, (int) y - 1, size + 2, size + 2);
            }
        }
    }
}