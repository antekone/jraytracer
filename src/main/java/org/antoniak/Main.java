package org.antoniak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("a1's raytracing demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 840);
            frame.setResizable(false);
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

    private volatile boolean wPressed = false;
    private volatile boolean aPressed = false;
    private volatile boolean sPressed = false;
    private volatile boolean dPressed = false;
    private volatile boolean zoomInPressed = false;
    private volatile boolean zoomOutPressed = false;
    private volatile boolean rotate1Pressed = false;
    private volatile boolean rotate2Pressed = false;

    private final int numWorkers;
    private final ExecutorService executor;

    private boolean cameraMoved = true;

    public OwnerDrawPanel(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setDoubleBuffered(true);
        requestFocusInWindow();

        numWorkers = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numWorkers);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_W:
                        wPressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        sPressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_A:
                        aPressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                        dPressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_MINUS:
                        zoomInPressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_EQUALS:
                        zoomOutPressed = true;
                        break;
                    case KeyEvent.VK_OPEN_BRACKET:
                        rotate1Pressed = true;
                        break;
                    case KeyEvent.VK_CLOSE_BRACKET:
                        rotate2Pressed = true;
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE, java.awt.event.KeyEvent.VK_Q:
                        System.exit(0);
                        break;
                }
            }
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_W:
                        wPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        sPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_A:
                        aPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                        dPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_MINUS:
                        zoomInPressed = false;
                        break;
                    case java.awt.event.KeyEvent.VK_EQUALS:
                        zoomOutPressed = false;
                        break;
                    case KeyEvent.VK_OPEN_BRACKET:
                        rotate1Pressed = false;
                        break;
                    case KeyEvent.VK_CLOSE_BRACKET:
                        rotate2Pressed = false;
                        break;
                }
            }
        });
    }

    private double cameraSpeed = 0.08;

    public void startRendering() {
        running = true;

        Thread renderThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            double nsPerFrame = 1_000_000_000.0 / 65.0;
            int frame = 0;
            while (running) {
                long now = System.nanoTime();
                if (now - lastTime >= nsPerFrame) {
                    double dx = 0, dy = 0, dz = 0;
                    double r = 0;

                    if (wPressed) dy += cameraSpeed;
                    if (sPressed) dy -= cameraSpeed;
                    if (aPressed) dx -= cameraSpeed;
                    if (dPressed) dx += cameraSpeed;
                    if (zoomInPressed) dz -= cameraSpeed;
                    if (zoomOutPressed) dz += cameraSpeed;
                    if (rotate1Pressed) r += cameraSpeed / 2;
                    if (rotate2Pressed) r -= cameraSpeed / 2;

                    if (dx != 0 || dy != 0) {
                        viewport.moveCamera(dx, dy);
                        cameraMoved = true;
                    }
                    if (dz != 0) {
                        viewport.moveCameraZ(dz);
                        cameraMoved = true;
                    }
                    if (r != 0) {
                        viewport.rotateCamera(r);
                        cameraMoved = true;
                    }
                    if (cameraMoved) {
                        updateImage(frame++);
                        repaint();
                        cameraMoved = false;
                    }

                    lastTime = now;

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
        int chunkSize = (int) Math.ceil(height / (double) numWorkers);
        java.util.List<Future<?>> futures = new java.util.ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            final int startY = i * chunkSize;
            final int endY = Math.min(height, (i + 1) * chunkSize);
            futures.add(executor.submit(() -> {
                for (int y = startY; y < endY; y++) {
                    double relY = y - height / 2.0;
                    for (int x = 0; x < width; x++) {
                        double relX = x - width / 2.0;
                        var color = viewport.traceRay(relX, relY, width, height, 1, Double.MAX_VALUE);
                        image.setRGB(x, height - y - 1, color.toSwingColor().getRGB());
                    }
                }
            }));
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);

        Graphics2D g2d = (Graphics2D) g.create();
        String fpsText = String.format("FPS: %.1f", currentFps);
        Font font = new Font("Monospaced", Font.BOLD, 18);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(fpsText);
        int textHeight = fm.getHeight();
        int x = width - textWidth - 10;
        int y = textHeight;


        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(x - 5, y - textHeight + 2, textWidth + 10, textHeight);

        g2d.setColor(Color.WHITE);
        g2d.drawString(fpsText, x, y);
        g2d.dispose();
    }
}