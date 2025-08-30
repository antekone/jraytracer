package org.antoniak;

public class DirectionalLight implements Light {
    private double intensity;
    private Vec3D direction;

    public DirectionalLight(double intensity, Vec3D direction) {
        this.intensity = intensity;
        this.direction = direction;
    }

    @Override
    public double calcIntensity(Scene s, Vec3D surfaceNormal, Vec3D lightRay, Vec3D cameraToPoint, double specular) {
        var closestObject = s.getClosestSphere(lightRay, direction, 0.001, Double.MAX_VALUE);
        if (closestObject != null)
            return 0.0;

        double dot = surfaceNormal.dot(direction);
        double i = 0.0;

        if (dot > 0) {
            // 1.0 - surfaceNormal length, it's assumed its length is 1.0
            i = intensity * dot / (1.0 * direction.length());
        }

        if (specular > 0) {
            Vec3D R = surfaceNormal.multiply(2.0).multiply(dot).subtract(direction);
            double r_dot_v = R.dot(cameraToPoint);
            if (r_dot_v > 0) {
                i += i * Math.pow(r_dot_v / (R.length() * cameraToPoint.length()), specular);
            }
        }

        return i;
    }
}
