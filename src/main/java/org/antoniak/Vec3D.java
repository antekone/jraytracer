package org.antoniak;

public class Vec3D {
    private double x;
    private double y;
    private double z;

    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dot(Vec3D other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public Vec3D normalized() {
        return new Vec3D(x / length(), y / length(), z / length());
    }

    public Vec3D add(Vec3D other) {
        return new Vec3D(x + other.x, y + other.y, z + other.z);
    }

    public Vec3D subtract(Vec3D other) {
        return new Vec3D(x - other.x, y - other.y, z - other.z);
    }

    public Vec3D multiply(double scalar) {
        return new Vec3D(x * scalar, y * scalar, z * scalar);
    }

    public Vec3D divide(double scalar) {
        return new Vec3D(x / scalar, y / scalar, z / scalar);
    }
}
