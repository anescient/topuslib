package net.chaosworship.topuslib.geom2d;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import net.chaosworship.topuslib.math.Spline;


@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue", "SameParameterValue"})
public class Vec2 implements Cloneable, Parcelable {

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

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Vec2(%f, %f)", x, y);
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

    public boolean epsilonEquals(Vec2 rhs, float epsilon) {
        return Math.abs(x - rhs.x) < epsilon && Math.abs(y - rhs.y) < epsilon;
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

    // set filtered value at c
    public Vec2 setSavitzkyGolay(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Vec2 e) {
        x = Spline.SavitzkyGolay(a.x, b.x, c.x, d.x, e.x);
        y = Spline.SavitzkyGolay(a.y, b.y, c.y, d.y, e.y);
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

    public Vec2 addScaledDifference(Vec2 lhs, Vec2 rhs, float scale) {
        x += (lhs.x - rhs.x) * scale;
        y += (lhs.y - rhs.y) * scale;
        return this;
    }

    // q is expected to be in [0, 1]
    // produce point along the line from a to b
    // q=0 gives a, q=1 gives b, q=0.5 gives midpoint of ab
    public Vec2 setMix(Vec2 a, Vec2 b, float q) {
        x = a.x + q * (b.x - a.x);
        y = a.y + q * (b.y - a.y);
        return this;
    }

    public static Vec2 mix(Vec2 a, Vec2 b, float q) {
        return new Vec2().setMix(a, b, q);
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

    public Vec2 scale(float xscale, float yscale) {
        x *= xscale;
        y *= yscale;
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

    public Vec2 clampMagnitude(float min, float max) {
        if(min > max) {
            throw new IllegalArgumentException();
        }
        float magSq = magnitudeSq();
        if(magSq == 0) {
            return this;
        }
        if(magSq < min * min) {
            scale(min / (float)Math.sqrt(magSq));
        } else if(magSq > max * max) {
            scale(max / (float)Math.sqrt(magSq));
        }
        return this;
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

    public static float distanceSq(Vec2 a, Vec2 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    public static float distance(Vec2 a, Vec2 b) {
        return (float)Math.sqrt(distanceSq(a, b));
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

    // reflect "about" normal
    // like reflecting off of surface with given normal
    public Vec2 reflect(Vec2 normal) {
        this.addScaled(normal, -2 * this.dot(normal));
        return this;
    }

    public Vec2 reflected(Vec2 normal) {
        return new Vec2(this).reflect(normal);
    }

    public double atan2() {
        if(x == 0 && y == 0) {
            return 0;
        }
        // match angle to unit() methods
        return Math.atan2(y, x);
    }

    public static Vec2 unit(double radians) {
        return new Vec2().setUnit(radians);
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

    public boolean inHalfPlane(Vec2 a, Vec2 b) {
        return ((x - b.x) * (a.y - b.y) - (a.x - b.x) * (y - b.y)) < 0;
    }

    public static boolean inHalfPlane(Vec2 p, Vec2 a, Vec2 b) {
        return ((p.x - b.x) * (a.y - b.y) - (a.x - b.x) * (p.y - b.y)) < 0;
    }

    ///////////////////////////////////////////////////////////
    // Parcelable

    protected Vec2(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    public static final Creator<Vec2> CREATOR = new Creator<Vec2>() {
        @Override
        public Vec2 createFromParcel(Parcel in) {
            return new Vec2(in);
        }

        @Override
        public Vec2[] newArray(int size) {
            return new Vec2[size];
        }
    };
}
