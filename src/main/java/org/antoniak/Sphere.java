package org.antoniak;

import java.awt.*;

public class Sphere {
    private LocalColor col;
    private double radius;
    private double radius2;
    private double specular; // 1000 = very shiny, 0 = matte
    private double reflective; // 0 = no reflection, 1 = mirror
    private Vec3D center;

    public Sphere(LocalColor col, double radius, Vec3D center, double specular, double reflective) {
        this.col = col;
        this.radius = radius;
        this.radius2 = radius * radius;
        this.center = center;
        this.specular = specular;
        this.reflective = reflective;
    }

    double[] intersectRay(Vec3D camera, Vec3D ray, double rayDot) {
        var oc = camera.subtract(center);
        var b = 2 * oc.dot(ray);
        var c = oc.dot(oc) - radius2;

        var discriminant = b * b - 4 * rayDot * c;
        if (discriminant < 0) {
            return null;
        } else {
            var sq = Math.sqrt(discriminant);
            var t1 = (-b - sq) / (2 * rayDot);
            var t2 = (-b + sq) / (2 * rayDot);
            return new double[] { t1, t2 };
        }
    }

    public LocalColor getColor() {
        return col;
    }

    public double getRadius() {
        return radius;
    }

    public Vec3D getCenter() {
        return center;
    }

    public double getSpecular() {
        return specular;
    }

    public double getReflective() {
        return reflective;
    }
}
