package org.antoniak;

public class AmbientLight implements Light {
    private final double intensity;

    public AmbientLight(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public double calcIntensity(Vec3D surfaceNormal, Vec3D lightRay) {
        return intensity;
    }
}
