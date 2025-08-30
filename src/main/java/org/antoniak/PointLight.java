package org.antoniak;

public class PointLight implements Light {
    private double intensity;
    private Vec3D position;

    public PointLight(double intensity, Vec3D position) {
        this.intensity = intensity;
        this.position = position;
    }

    @Override
    public double calcIntensity(Vec3D surfaceNormal, Vec3D point) {
        var lightRayToPosition = position.subtract(point);
        double dot = surfaceNormal.dot(lightRayToPosition);

        if (dot > 0) {
            // 1.0 - surfaceNormal length, it's assumed its length is 1.0
            return intensity * dot / (1.0 * lightRayToPosition.length());
        }

        return 0.0;
    }
}
