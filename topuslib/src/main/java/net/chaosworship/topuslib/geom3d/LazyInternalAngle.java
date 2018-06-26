package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.geom2d.Vec2;


public class LazyInternalAngle {

    private final Vec3 mCross;
    private final float mCrossMag;
    private final float mDot;
    private float mSineDivisor;
    private double mRadians;

    public LazyInternalAngle(Vec2 a, Vec2 b) {
        this(new Vec3(a.x, a.y, 0), new Vec3(b.x, b.y, 0));
    }

    public LazyInternalAngle(Vec3 a, Vec3 b) {
        mCross = Vec3.cross(a, b);
        mCrossMag = mCross.magnitude();
        mDot = a.dot(b);
        mSineDivisor = Float.NaN;
        mRadians = Double.NaN;
    }

    public float dot() {
        return mDot;
    }

    public Vec3 cross() {
        return mCross;
    }

    public float crossMagnitude() {
        return mCrossMag;
    }

    public float sine() {
        requireDivisor();
        return mCrossMag * mSineDivisor;
    }

    public float cosine() {
        requireDivisor();
        return mDot * mSineDivisor;
    }

    public double radians() {
        if(Double.isNaN(mRadians)) {
            mRadians = Math.atan2(mCrossMag, mDot);
        }
        return mRadians;
    }

    private void requireDivisor() {
        if(Float.isNaN(mSineDivisor)) {
            mSineDivisor = 1.0f / (float)Math.sqrt(mCrossMag * mCrossMag + mDot * mDot);
        }
    }
}
