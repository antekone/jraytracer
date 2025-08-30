package org.antoniak;

public class Scene {
    public static class ClosestObject {
        Sphere sphere;
        double t;

        public ClosestObject(Sphere sphere, double t) {
            this.sphere = sphere;
            this.t = t;
        }
    }

    private final Sphere[] spheres = {
        // 8 spheres in a circle (radius 3, y = 0, z = 8)
        new Sphere(new LocalColor(255, 0, 0), 1.0, new Vec3D(3, 0, 8), 500, 0.2),
        new Sphere(new LocalColor(255, 128, 0), 1.0, new Vec3D(2.12, 2.12, 8), 500, 0.2),
        new Sphere(new LocalColor(255, 255, 0), 1.0, new Vec3D(0, 3, 8), 500, 0.2),
        new Sphere(new LocalColor(0, 255, 0), 1.0, new Vec3D(-2.12, 2.12, 8), 500, 0.2),
        new Sphere(new LocalColor(0, 255, 255), 1.0, new Vec3D(-3, 0, 8), 500, 0.2),

        // ground
        new Sphere(new LocalColor(200, 200, 200), 5000.0, new Vec3D(0, -5001, 0), 1000, 0.5),
    };

    private final Light[] lights = {
        new PointLight(0.8, new Vec3D(0, 0, 8)), // center of the circle
        new DirectionalLight(0.3, new Vec3D(1, 4, 4)),
        new AmbientLight(0.2)
    };

    public ClosestObject getClosestSphere(Vec3D camera, Vec3D ray, double minT, double maxT) {
        double t = Double.MAX_VALUE;
        Sphere closestSphere = null;
        double rayDot = ray.dot(ray);

        for (final var s : spheres) {
            double[] candidateT = s.intersectRay(camera, ray, rayDot);
            if (candidateT == null)
                continue;

            if (candidateT[0] < t && candidateT[0] > minT && candidateT[0] < maxT) {
                t = candidateT[0];
                closestSphere = s;
            }

            if (candidateT[1] < t && candidateT[1] > minT && candidateT[1] < maxT) {
                t = candidateT[1];
                closestSphere = s;
            }
        }

        if (closestSphere == null)
            return null;

        return new ClosestObject(closestSphere, t);
    }

    public LocalColor traceRay(Vec3D camera, Vec3D ray, double minT, double maxT, int recursionDepth) {
        var closestObject = getClosestSphere(camera, ray, minT, maxT);
        if (closestObject == null)
            return LocalColor.BLACK;

        var closestSphere = closestObject.sphere;
        double t = closestObject.t;

        // lights, shadows

        Vec3D visiblePoint = camera.add(ray.multiply(t));
        Vec3D surfaceNormal = visiblePoint.subtract(closestSphere.getCenter()).normalized();
        var originalColor = closestSphere.getColor();
        double specular = closestSphere.getSpecular();
        Vec3D rayNegated = ray.negate();

        double lightIntensity = 0.0;
        for (final var l : lights) {
            lightIntensity += l.calcIntensity(this, surfaceNormal, visiblePoint, rayNegated, specular);
        }

        var illuminatedColor = originalColor.multiply(lightIntensity);

        // reflection
        if (closestSphere.getReflective() <= 0 || recursionDepth <= 0) {
            return illuminatedColor;
        }

        double n_dot_rn = surfaceNormal.dot(rayNegated);
        Vec3D R = surfaceNormal.multiply(2.0).multiply(n_dot_rn).subtract(rayNegated);
        var reflectedColor = traceRay(visiblePoint, R, 0.001, Double.MAX_VALUE, recursionDepth - 1);

        var localContribution = illuminatedColor.multiply(1 - closestSphere.getReflective());
        var reflectedContribution = reflectedColor.multiply(closestSphere.getReflective());

        return localContribution.add(reflectedContribution);
    }
}
