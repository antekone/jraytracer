package org.antoniak;

public class DirectionalLight implements Light {
    private double intensity;
    private Vec3D direction;

    public DirectionalLight(double intensity, Vec3D direction) {
        this.intensity = intensity;
        this.direction = direction;
    }

    @Override
    public double calcIntensity(Vec3D surfaceNormal, Vec3D lightRay) {
        double dot = surfaceNormal.dot(direction);

        if (dot > 0) {
            // 1.0 - surfaceNormal length, it's assumed its length is 1.0
            return intensity * dot / (1.0 * direction.length());
        }

        return 0.0;
    }
}
