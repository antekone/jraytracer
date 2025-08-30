package org.antoniak;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("a1's raytracing demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 840);
            frame.setResizable(false); // Disallow resizing
            frame.setLocationRelativeTo(null);

            OwnerDrawPanel panel = new OwnerDrawPanel(800, 800);
            frame.add(panel);
            frame.setVisible(true);
            panel.startRendering();
        });
    }
}

class OwnerDrawPanel extends JPanel {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private volatile boolean running = false;
    private volatile double currentFps = 0.0;
    private int frameCounter = 0;
    private long lastFpsTime = System.nanoTime();

    private Viewport viewport = new Viewport();

    public OwnerDrawPanel(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(width, height));
    }

    public void startRendering() {
        running = true;
        Thread renderThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double nsPerFrame = 1_000_000_000.0 / 65.0; // 30 FPS
            int frame = 0;
            while (running) {
                long now = System.nanoTime();
                if (now - lastTime >= nsPerFrame) {
                    updateImage(frame++);
                    repaint();
                    lastTime = now;
                    // FPS calculation
                    frameCounter++;
                    if (now - lastFpsTime >= 1_000_000_000L) {
                        currentFps = frameCounter * 1_000_000_000.0 / (now - lastFpsTime);
                        frameCounter = 0;
                        lastFpsTime = now;
                    }
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        });
        renderThread.setDaemon(true);
        renderThread.start();
    }

    private void updateImage(int frame) {
        Math.abs(0);

//        // Example: fill with animated color gradient
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int r = (x + frame) % 256;
//                int g = (y + frame) % 256;
//                int b = (x + y + frame) % 256;
//                int a = 255;
//                int color = (a << 24) | (r << 16) | (g << 8) | b;
//                image.setRGB(x, y, color);
//            }
//        }

        for (int y = 0; y < height; y++) {
            double relY = y;
            relY -= height / 2.0;

            for (int x = 0; x < width; x++) {
                double relX = x;
                relX -= width / 2.0;

                var color = viewport.traceRay(relX, relY, width, height, 1, Double.MAX_VALUE);
                image.setRGB(x, height - y - 1, color.getRGB());
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
        // Draw FPS counter in top right
        Graphics2D g2d = (Graphics2D) g.create();
        String fpsText = String.format("FPS: %.1f", currentFps);
        Font font = new Font("Monospaced", Font.BOLD, 18);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(fpsText);
        int textHeight = fm.getHeight();
        int x = width - textWidth - 10;
        int y = textHeight;
        // Draw background for readability
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(x - 5, y - textHeight + 2, textWidth + 10, textHeight);
        // Draw FPS text
        g2d.setColor(Color.WHITE);
        g2d.drawString(fpsText, x, y);
        g2d.dispose();
    }
}