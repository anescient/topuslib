package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;
import android.opengl.Matrix;

import net.chaosworship.topuslib.math.Spline;
import net.chaosworship.topuslib.tuple.IntTriple;


@SuppressWarnings({"unused", "WeakerAccess"})
public class Vec3 implements Cloneable {

    public float x;
    public float y;
    public float z;

    public Vec3() {
        this(0, 0, 0);
    }

    public Vec3(Vec3 source) {
        this.x = source.x;
        this.y = source.y;
        this.z = source.z;
    }

    public Vec3(IntTriple xyz) {
        x = xyz.a;
        y = xyz.b;
        z = xyz.c;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Vec3(%f, %f, %f)", x, y, z);
    }

    @Override
    protected Object clone()
            throws CloneNotSupportedException {
        super.clone();
        return new Vec3(this);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        Vec3 v = (Vec3)rhs;
        return x == v.x && y == v.y && z == v.z;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(x) ^ Float.floatToIntBits(y) ^ Float.floatToIntBits(z);
    }

    public boolean epsilonEquals(Vec3 rhs, float epsilon) {
        return Math.abs(x - rhs.x) < epsilon && Math.abs(y - rhs.y) < epsilon && Math.abs(z - rhs.z) < epsilon;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Vec3 setZero() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }

    public Vec3 set(Vec3 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        return this;
    }

    public Vec3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    // u in [0,1]
    public Vec3 setCubicBSpline(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float u) {
        x = Spline.CubicBSpline(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSpline(a.y, b.y, c.y, d.y, u);
        z = Spline.CubicBSpline(a.z, b.z, c.z, d.z, u);
        return this;
    }

    // first derivative (tangent)
    public Vec3 setCubicBSplineFirst(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float u) {
        x = Spline.CubicBSplineFirst(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSplineFirst(a.y, b.y, c.y, d.y, u);
        z = Spline.CubicBSplineFirst(a.z, b.z, c.z, d.z, u);
        return this;
    }

    // second derivative (curvature)
    public Vec3 setCubicBSplineSecond(Vec3 a, Vec3 b, Vec3 c, Vec3 d, float u) {
        x = Spline.CubicBSplineSecond(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSplineSecond(a.y, b.y, c.y, d.y, u);
        z = Spline.CubicBSplineSecond(a.z, b.z, c.z, d.z, u);
        return this;
    }

    // u in [0,1]
    public Vec3 setQuarticBSpline(Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec3 e, float u) {
        x = Spline.QuarticBSpline(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSpline(a.y, b.y, c.y, d.y, e.y, u);
        z = Spline.QuarticBSpline(a.z, b.z, c.z, d.z, e.z, u);
        return this;
    }

    // first derivative (tangent)
    public Vec3 setQuarticBSplineFirst(Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec3 e, float u) {
        x = Spline.QuarticBSplineFirst(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSplineFirst(a.y, b.y, c.y, d.y, e.y, u);
        z = Spline.QuarticBSplineFirst(a.z, b.z, c.z, d.z, e.z, u);
        return this;
    }

    // second derivative (curvature)
    public Vec3 setQuarticBSplineSecond(Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec3 e, float u) {
        x = Spline.QuarticBSplineSecond(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSplineSecond(a.y, b.y, c.y, d.y, e.y, u);
        z = Spline.QuarticBSplineSecond(a.z, b.z, c.z, d.z, e.z, u);
        return this;
    }

    // set filtered value at c
    public Vec3 setSavitzkyGolay(Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec3 e) {
        x = Spline.SavitzkyGolay(a.x, b.x, c.x, d.x, e.x);
        y = Spline.SavitzkyGolay(a.y, b.y, c.y, d.y, e.y);
        z = Spline.SavitzkyGolay(a.z, b.z, c.z, d.z, e.z);
        return this;
    }

    public static Vec3 midpoint(Vec3 a, Vec3 b) {
        return new Vec3().setMidpoint(a, b);
    }

    public Vec3 setMidpoint(Vec3 a, Vec3 b) {
        x = (a.x + b.x) * 0.5f;
        y = (a.y + b.y) * 0.5f;
        z = (a.z + b.z) * 0.5f;
        return this;
    }

    public Vec3 add(Vec3 rhs) {
        x += rhs.x;
        y += rhs.y;
        z += rhs.z;
        return this;
    }

    public Vec3 add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vec3 subtract(Vec3 rhs) {
        x -= rhs.x;
        y -= rhs.y;
        z -= rhs.z;
        return this;
    }

    public Vec3 subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vec3 addScaled(Vec3 increment, float scale) {
        x += increment.x * scale;
        y += increment.y * scale;
        z += increment.z * scale;
        return this;
    }

    // q is expected to be in [0, 1]
    // produce point along the line from a to b
    // q=0 gives a, q=1 gives b, q=0.5 gives midpoint of ab
    public Vec3 setMix(Vec3 a, Vec3 b, float q) {
        x = a.x + q * (b.x - a.x);
        y = a.y + q * (b.y - a.y);
        z = a.z + q * (b.z - a.z);
        return this;
    }

    public Vec3 negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public Vec3 negated() {
        return new Vec3(this).negate();
    }

    public Vec3 scale(float multiplier) {
        x *= multiplier;
        y *= multiplier;
        z *= multiplier;
        return this;
    }

    public Vec3 scaleInverse(float divisor) {
        x /= divisor;
        y /= divisor;
        z /= divisor;
        return this;
    }

    public Vec3 scaled(float multiplier) {
        return new Vec3(x * multiplier, y * multiplier, z * multiplier);
    }

    public Vec3 sum(Vec3 rhs) {
        return new Vec3(x + rhs.x, y + rhs.y, z + rhs.z);
    }

    public Vec3 setSum(Vec3 a, Vec3 b) {
        x = a.x + b.x;
        y = a.y + b.y;
        z = a.z + b.z;
        return this;
    }

    public Vec3 difference(Vec3 rhs) {
        return new Vec3(x - rhs.x, y - rhs.y, z - rhs.z);
    }

    public Vec3 setDifference(Vec3 a, Vec3 b) {
        x = a.x - b.x;
        y = a.y - b.y;
        z = a.z - b.z;
        return this;
    }

    public float magnitudeSq() {
        return x * x + y * y + z * z;
    }

    public float magnitude() {
        return (float)Math.sqrt(magnitudeSq());
    }

    public static float distanceSq(Vec3 a, Vec3 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public static float distance(Vec3 a, Vec3 b) {
        return (float)Math.sqrt(distanceSq(a, b));
    }

    public static Vec3 cross(Vec3 lhs, Vec3 rhs) {
        return new Vec3().setCross(lhs, rhs);
    }

    public Vec3 setCross(Vec3 lhs, Vec3 rhs) {
        x = lhs.y * rhs.z - lhs.z * rhs.y;
        y = lhs.z * rhs.x - lhs.x * rhs.z;
        z = lhs.x * rhs.y - lhs.y * rhs.x;
        return this;
    }

    public float dot(Vec3 rhs) {
        return x * rhs.x + y * rhs.y + z * rhs.z;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Vec3 setArbitraryPerpendicular(Vec3 v) {
        if(v.isZero()) {
            set(0, 0, 1);
        } else {
            Vec3 u = new Vec3();
            if(v.x == 0) {
                u.set(v.x, -v.z, v.y);
            } else {
                u.set(-v.y, -v.x, v.z);
            }
            setCross(u, v);
        }

        return this;
    }

    public static Vec3 arbitraryPerpendicular(Vec3 v) {
        return new Vec3().setArbitraryPerpendicular(v);
    }

    public Vec3 normalize() {
        float m = magnitude();
        if(m > 0) {
            x /= m;
            y /= m;
            z /= m;
        }
        return this;
    }

    public Vec3 normalized() {
        return new Vec3(this).normalize();
    }

    ////////////////////////////////////////////////////

    public static void setLookAtM(float[] m, Vec3 eye, Vec3 center, Vec3 up) {
        Matrix.setLookAtM(m, 0,
                eye.x, eye.y, eye.z,
                center.x, center.y, center.z,
                up.x, up.y, up.z);
    }
}
