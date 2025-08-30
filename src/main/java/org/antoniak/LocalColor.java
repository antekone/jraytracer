package org.antoniak;

import java.awt.*;

public class LocalColor {
    int r;
    int g;
    int b;

    public static LocalColor BLACK = new LocalColor(0, 0, 0);
    public static LocalColor WHITE = new LocalColor(255, 255, 255);

    public LocalColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public LocalColor multiply(double scalar) {
        int nr = (int) (scalar * r);
        int ng = (int) (scalar * g);
        int nb = (int) (scalar * b);

        if (nr < 0) nr = 0;
        if (nr > 255) nr = 255;

        if (ng < 0) ng = 0;
        if (ng > 255) ng = 255;

        if (nb < 0) nb = 0;
        if (nb > 255) nb = 255;

        return new LocalColor(nr, ng, nb);
    }
    
    public LocalColor add(LocalColor other) {
        int nr = r + other.r;
        int ng = g + other.g;
        int nb = b + other.b;

        if (nr < 0) nr = 0;
        if (nr > 255) nr = 255;

        if (ng < 0) ng = 0;
        if (ng > 255) ng = 255;

        if (nb < 0) nb = 0;
        if (nb > 255) nb = 255;

        return new LocalColor(nr, ng, nb);
    }

    public Color toSwingColor() {
        return new Color(r, g, b);
    }
}
