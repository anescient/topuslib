package net.chaosworship.topuslib.math;


// rotate in-place
public class Vec2Rotator {

    private float mCosine;
    private float mSine;

    public Vec2Rotator() {
        mCosine = 0;
        mSine = 1;
    }

    public Vec2Rotator(float radians) {
        setRadians(radians);
    }

    public void setRadians(float radians) {
        mCosine = (float)Math.cos(radians);
        mSine = (float)Math.sin(radians);
    }

    public void rotate(Vec2 v) {
        float x = mCosine * v.x - mSine * v.y;
        float y = mSine * v.x + mCosine * v.y;
        v.set(x, y);
    }

    public void rotateAbout(Vec2 v, Vec2 about) {
        float x = about.x + mCosine * (v.x - about.x) - mSine * (v.y - about.y);
        float y = about.y + mSine * (v.x - about.x) + mCosine * (v.y - about.y);
        v.set(x, y);
    }
}
