package org.antoniak;

import java.awt.*;

public class Viewport {
    private double viewportSize = 1.0;
    private double projectionPlaneZ = 1.0;
    private Vec3D cameraPosition = new Vec3D(0, 0, 0);
    private Scene scene = new Scene();

    private Vec3D canvasToViewport(double x, double y, double w, double h) {
        return new Vec3D(x * viewportSize / w, y * viewportSize / h, projectionPlaneZ);
    }

    public Color traceRay(double x, double y, double w, double h, double minDistance, double maxDistance) {
        var direction = canvasToViewport(x, y, w, h);
        return scene.traceRay(cameraPosition, direction, minDistance, maxDistance);
    }
}
