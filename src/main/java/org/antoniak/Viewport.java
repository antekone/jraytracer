package org.antoniak;

import java.awt.*;

public class Viewport {
    private double viewportSize = 1.0;
    private double projectionPlaneZ = 1.0;
    private Vec3D cameraPosition = new Vec3D(0, 0, 0);
    private double cameraRotation = 0.0;
    private Scene scene = new Scene();

    private Vec3D canvasToViewport(double x, double y, double w, double h) {
        return new Vec3D(x * viewportSize / w, y * viewportSize / h, projectionPlaneZ);
    }

    private double[] makeMatrixRotationZ(double angle) {
        double[] m = new double[9];
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        m[0] = cos;
        m[1] = -sin;
        m[2] = 0;
        m[3] = sin;
        m[4] = cos;
        m[5] = 0;
        m[6] = 0;
        m[7] = 0;
        m[8] = 1;

        return m;
    }
    
    private double[] makeMatrixRotationX(double angle) {
        double[] m = new double[9];
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        m[0] = 1;
        m[1] = 0;
        m[2] = 0;
        m[3] = 0;
        m[4] = cos;
        m[5] = -sin;
        m[6] = 0;
        m[7] = sin;
        m[8] = cos;

        return m;
    }
    
    private double[] makeMatrixRotationY(double angle) {
        double[] m = new double[9];
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        m[0] = cos;
        m[1] = 0;
        m[2] = sin;
        m[3] = 0;
        m[4] = 1;
        m[5] = 0;
        m[6] = -sin;
        m[7] = 0;
        m[8] = cos;

        return m;
    }

    public LocalColor traceRay(double x, double y, double w, double h, double minDistance, double maxDistance) {
        var cameraRotationMatrix = makeMatrixRotationY(cameraRotation);
        var direction = canvasToViewport(x, y, w, h).multiplyMatrix(cameraRotationMatrix);
        return scene.traceRay(cameraPosition, direction, minDistance, maxDistance, 1);
    }

    public void moveCamera(double dx, double dy) {
        cameraPosition = new Vec3D(cameraPosition.x + dx, cameraPosition.y + dy, cameraPosition.z);
    }

    public void moveCameraZ(double dz) {
        cameraPosition = new Vec3D(cameraPosition.x, cameraPosition.y, cameraPosition.z + dz);
    }

    public void rotateCamera(double angle) {
        cameraRotation += angle;
    }
}
