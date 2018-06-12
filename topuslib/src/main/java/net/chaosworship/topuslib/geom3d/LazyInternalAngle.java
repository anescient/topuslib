package net.chaosworship.topuslib.geom3d;


public class LazyInternalAngle {

    private final Vec3 mCross;
    private final float mCrossMag;
    private final float mDot;
    private float mSineDivisor;
    private double mRadians;

    public LazyInternalAngle(Vec3 a, Vec3 b) {
        mCross = Vec3.cross(a, b);
        mCrossMag = mCross.magnitude();
        mDot = a.dot(b);
        mSineDivisor = Float.NaN;
        mRadians = Double.NaN;
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
