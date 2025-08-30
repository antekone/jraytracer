package org.antoniak;

public class AmbientLight implements Light {
    private final double intensity;

    public AmbientLight(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public double calcIntensity(Scene s, Vec3D surfaceNormal, Vec3D lightRay, Vec3D cameraToPoint, double specular) {
        return intensity;
    }
}
