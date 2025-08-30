package org.antoniak;

import java.awt.*;

public class Sphere {
    private Color col;
    private double radius;
    private Vec3D center;

    public Sphere(Color col, double radius, Vec3D center) {
        this.col = col;
        this.radius = radius;
        this.center = center;
    }

    double[] intersectRay(Vec3D camera, Vec3D ray) {
        var oc = camera.subtract(center);
        var a = ray.dot(ray);
        var b = 2 * oc.dot(ray);
        var c = oc.dot(oc) - radius * radius;

        var discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return new double[] { Double.MAX_VALUE, Double.MAX_VALUE };
        } else {
            var t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
            var t2 = (-b + Math.sqrt(discriminant)) / (2 * a);
            return new double[] { t1, t2 };
        }
    }

    public Color getColor() {
        return col;
    }

    public double getRadius() {
        return radius;
    }

    public Vec3D getCenter() {
        return center;
    }
}
