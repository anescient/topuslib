package net.chaosworship.topuslib.geom3d.transform;

import net.chaosworship.topuslib.geom3d.Vec3;


public class AxisAngleRotator {

    private final Vec3 mAxis;
    private float mCos;
    private float mSin;

    private final Vec3 tempCross;
    private final Vec3 tempV;

    public AxisAngleRotator() {
        this(new Vec3(0, 0, 1), 1, 0);
    }

    public AxisAngleRotator(Vec3 unitAxis, double angle) {
        this(unitAxis, (float)Math.cos(angle), (float)Math.sin(angle));
    }

    private AxisAngleRotator(Vec3 unitAxis, float cos, float sin) {
        mAxis = unitAxis;
        mCos = cos;
        mSin = sin;
        tempCross = new Vec3();
        tempV = new Vec3();
    }

    // set the rotation which transforms a into b
    public void capture(Vec3 a, Vec3 b) {
        mAxis.setCross(a, b);
        float axisNorm = mAxis.magnitude();
        if(axisNorm == 0) {
            mAxis.set(0, 0, 1);
            mCos = 1;
            mSin = 0;
            return;
        }

        // with trig
        // double angle = Math.atan2(axisNorm, a.dot(b));
        // return new AxisAngleRotator(axis.scaleInverse(axisNorm), angle);

        // without trig
        float dot = a.dot(b);
        float divide = (float)(1 / Math.sqrt(axisNorm * axisNorm + dot * dot));
        mCos = dot * divide;
        mSin = axisNorm * divide;
        mAxis.scaleInverse(axisNorm);
    }

    // Rodrigues' rotation formula
    public Vec3 rotated(Vec3 v) {
        tempCross.setCross(mAxis, v);
        return new Vec3()
            .addScaled(v, mCos)
            .addScaled(tempCross, mSin)
            .addScaled(mAxis, (mAxis.dot(v) * (1 - mCos)));
    }

    public void rotate(Vec3 v) {
        tempV.set(v);
        tempCross.setCross(mAxis, tempV);
        v.setZero()
            .addScaled(tempV, mCos)
            .addScaled(tempCross, mSin)
            .addScaled(mAxis, mAxis.dot(tempV) * (1 - mCos));
    }
}
