package net.chaosworship.topuslib.geom2d;


import net.chaosworship.topuslib.math.Spline;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue", "SameParameterValue"})
public class Vec2 {

    public float x;
    public float y;

    public Vec2() {
        this(0, 0);
    }

    public Vec2(Vec2 source) {
        this.x = source.x;
        this.y = source.y;
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected Object clone()
            throws CloneNotSupportedException {
        super.clone();
        return new Vec2(this);
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        Vec2 v = (Vec2)rhs;
        return x == v.x && y == v.y;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(x) ^ Float.floatToIntBits(y);
    }

    public boolean lessThanXY(Vec2 rhs) {
        return x == rhs.x ? y < rhs.y : x < rhs.x;
    }

    public boolean lessThanYX(Vec2 rhs) {
        return y == rhs.y ? x < rhs.x : y < rhs.y;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Vec2 setZero() {
        x = 0;
        y = 0;
        return this;
    }

    public Vec2 set(Vec2 other) {
        x = other.x;
        y = other.y;
        return this;
    }

    public Vec2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    // u in [0,1]
    public Vec2 setCubicBSpline(Vec2 a, Vec2 b, Vec2 c, Vec2 d, float u) {
        x = Spline.CubicBSpline(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSpline(a.y, b.y, c.y, d.y, u);
        return this;
    }

    // first derivative (tangent)
    public Vec2 setCubicBSplineFirst(Vec2 a, Vec2 b, Vec2 c, Vec2 d, float u) {
        x = Spline.CubicBSplineFirst(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSplineFirst(a.y, b.y, c.y, d.y, u);
        return this;
    }

    // second derivative (curvature)
    public Vec2 setCubicBSplineSecond(Vec2 a, Vec2 b, Vec2 c, Vec2 d, float u) {
        x = Spline.CubicBSplineSecond(a.x, b.x, c.x, d.x, u);
        y = Spline.CubicBSplineSecond(a.y, b.y, c.y, d.y, u);
        return this;
    }

    // u in [0,1]
    public Vec2 setQuarticBSpline(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Vec2 e, float u) {
        x = Spline.QuarticBSpline(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSpline(a.y, b.y, c.y, d.y, e.y, u);
        return this;
    }

    // first derivative (tangent)
    public Vec2 setQuarticBSplineFirst(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Vec2 e, float u) {
        x = Spline.QuarticBSplineFirst(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSplineFirst(a.y, b.y, c.y, d.y, e.y, u);
        return this;
    }

    // second derivative (curvature)
    public Vec2 setQuarticBSplineSecond(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Vec2 e, float u) {
        x = Spline.QuarticBSplineSecond(a.x, b.x, c.x, d.x, e.x, u);
        y = Spline.QuarticBSplineSecond(a.y, b.y, c.y, d.y, e.y, u);
        return this;
    }

    public static Vec2 midpoint(Vec2 a, Vec2 b) {
        return new Vec2().setMidpoint(a, b);
    }

    public Vec2 setMidpoint(Vec2 a, Vec2 b) {
        x = (a.x + b.x) * 0.5f;
        y = (a.y + b.y) * 0.5f;
        return this;
    }

    public Vec2 add(Vec2 rhs) {
        x += rhs.x;
        y += rhs.y;
        return this;
    }

    public Vec2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vec2 subtract(Vec2 rhs) {
        x -= rhs.x;
        y -= rhs.y;
        return this;
    }

    public Vec2 subtract(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec2 addScaled(Vec2 increment, float scale) {
        x += increment.x * scale;
        y += increment.y * scale;
        return this;
    }

    // q is expected to be in [0, 1]
    // produce point along the line from a to b
    // q=0 gives a, q=1 gives b, q=0.5 gives midpoint of ab
    public static Vec2 mix(Vec2 a, Vec2 b, float q) {
        Vec2 v = new Vec2();
        v.x = a.x + q * (b.x - a.x);
        v.y = a.y + q * (b.y - a.y);
        return v;
    }

    public Vec2 negate() {
        x = -x;
        y = -y;
        return this;
    }

    public Vec2 negated() {
        return new Vec2(this).negate();
    }

    public Vec2 scale(float multiplier) {
        x *= multiplier;
        y *= multiplier;
        return this;
    }

    public Vec2 scaleInverse(float divisor) {
        x /= divisor;
        y /= divisor;
        return this;
    }

    public Vec2 scaled(float multiplier) {
        return new Vec2(x * multiplier, y * multiplier);
    }

    public Vec2 sum(Vec2 rhs) {
        return new Vec2(x + rhs.x, y + rhs.y);
    }

    public static Vec2 sum(Vec2 lhs, Vec2 rhs) {
        return lhs.sum(rhs);
    }

    public Vec2 setSum(Vec2 a, Vec2 b) {
        x = a.x + b.x;
        y = a.y + b.y;
        return this;
    }

    public Vec2 difference(Vec2 rhs) {
        return new Vec2(x - rhs.x, y - rhs.y);
    }

    public static Vec2 difference(Vec2 lhs, Vec2 rhs) {
        return lhs.difference(rhs);
    }

    public Vec2 setDifference(Vec2 a, Vec2 b) {
        x = a.x - b.x;
        y = a.y - b.y;
        return this;
    }

    public float magnitudeSq() {
        return x * x + y * y;
    }

    public float magnitude() {
        return (float)Math.sqrt(magnitudeSq());
    }

    public static float distance(Vec2 a, Vec2 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public float cross(Vec2 rhs) {
        return this.x * rhs.y - this.y * rhs.x;
    }

    public float dot(Vec2 rhs) {
        return this.x * rhs.x + this.y * rhs.y;
    }

    public float dotNegated(Vec2 rhs) {
        return this.x * -rhs.x + this.y * -rhs.y;
    }

    public Vec2 normalize() {
        float m = magnitude();
        if(m > 0) {
            x /= m;
            y /= m;
        }
        return this;
    }

    public Vec2 normalized() {
        Vec2 norm = new Vec2(this);
        norm.normalize();
        return norm;
    }

    public Vec2 setNormal(Vec2 a, Vec2 b) {
        setDifference(b, a);
        normalize();
        return this;
    }

    public static Vec2 unit(float radians) {
        return new Vec2().setUnit(radians);
    }

    public static Vec2 unit(double radians) {
        return new Vec2().setUnit(radians);
    }

    public Vec2 setUnit(float radians) {
        x = (float)Math.cos(radians);
        y = (float)Math.sin(radians);
        return this;
    }

    public Vec2 setUnit(double radians) {
        x = (float)Math.cos(radians);
        y = (float)Math.sin(radians);
        return this;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Vec2 rotate90() {
        float xx = x;
        x = y;
        y = -xx;
        return this;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Vec2 rotated90() {
        return new Vec2(y, -x);
    }

    public Vec2 rotate90about(Vec2 about) {
        float xx = about.x - y + about.y;
        y = about.y + x - about.x;
        x = xx;
        return this;
    }
}
