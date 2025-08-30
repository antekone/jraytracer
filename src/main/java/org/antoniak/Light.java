package org.antoniak;

public interface Light {
    double calcIntensity(Scene s, Vec3D surfaceNormal, Vec3D lightRay, Vec3D cameraToPoint, double specular);
}
