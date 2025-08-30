package org.antoniak;

public interface Light {
    double calcIntensity(Vec3D surfaceNormal, Vec3D lightRay);
}
