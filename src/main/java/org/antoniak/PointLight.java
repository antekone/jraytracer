package org.antoniak;

public class PointLight implements Light {
    private double intensity;
    private Vec3D position;

    public PointLight(double intensity, Vec3D position) {
        this.intensity = intensity;
        this.position = position;
    }

    @Override
    public double calcIntensity(Scene s, Vec3D surfaceNormal, Vec3D point, Vec3D cameraToPoint, double specular) {
        var lightRayToPosition = position.subtract(point);
        var closestObject = s.getClosestSphere(point, lightRayToPosition, 0.001, 1.0);
        if (closestObject != null)
            return 0.0;

        double dot = surfaceNormal.dot(lightRayToPosition);
        double i = 0.0;

        if (dot > 0) {
            // 1.0 - surfaceNormal length, it's assumed its length is 1.0
            i = intensity * dot / (1.0 * lightRayToPosition.length());
        }

        if (specular > 0) {
            Vec3D R = surfaceNormal.multiply(2.0).multiply(dot).subtract(lightRayToPosition);
            double r_dot_v = R.dot(cameraToPoint);
            if (r_dot_v > 0) {
                i += i * Math.pow(r_dot_v / (R.length() * cameraToPoint.length()), specular);
            }
        }

        return i;
    }
}
