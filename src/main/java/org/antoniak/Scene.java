package org.antoniak;

import java.awt.*;

public class Scene {
    private final Sphere[] spheres = {
        new Sphere(new Color(255, 0, 0), 1.0, new Vec3D(0, -1, 3)),
        new Sphere(new Color(0, 255, 0), 1.0, new Vec3D(-2, 0, 4)),
        new Sphere(new Color(0, 0, 255), 1.0, new Vec3D(2, 0, 4)),
        new Sphere(new Color(255, 255, 0), 5000.0, new Vec3D(0, -5001, 0)),
    };

    private final Light[] lights = {
            new DirectionalLight(0.2, new Vec3D(1, 4, 4)),
            new PointLight(0.6, new Vec3D(2, 1, 0)),
            new AmbientLight(0.2)
    };

    public Color traceRay(Vec3D camera, Vec3D ray, double minT, double maxT) {
        double t = Double.MAX_VALUE;
        Sphere closestSphere = null;

        for (final var s : spheres) {
            double[] candidateT = s.intersectRay(camera, ray);

            if (candidateT[0] < t && candidateT[0] > minT && candidateT[0] < maxT) {
                t = candidateT[0];
                closestSphere = s;
            }

            if (candidateT[1] < t && candidateT[1] > minT && candidateT[1] < maxT) {
                t = candidateT[1];
                closestSphere = s;
            }
        }

        if (closestSphere == null) return Color.WHITE;

        Vec3D visiblePoint = camera.add(ray.multiply(t));
        Vec3D surfaceNormal = visiblePoint.subtract(closestSphere.getCenter()).normalized();
        Color originalColor = closestSphere.getColor();

        double lightIntensity = 0.0;
        for (final var l : lights) {
            lightIntensity += l.calcIntensity(surfaceNormal, visiblePoint);
        }

        int red, green, blue;
        red = (int) (originalColor.getRed() * lightIntensity);
        green = (int) (originalColor.getGreen() * lightIntensity);
        blue = (int) (originalColor.getBlue() * lightIntensity);

        if (red > 255) red = 255;
        if (green > 255) green = 255;
        if (blue > 255) blue = 255;

        return new Color(red, green, blue);
    }
}
