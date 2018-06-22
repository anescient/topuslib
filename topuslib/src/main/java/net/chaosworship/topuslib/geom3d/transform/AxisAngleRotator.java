package net.chaosworship.topuslib.geom3d.transform;

import net.chaosworship.topuslib.geom3d.Vec3;


public class AxisAngleRotator {

    private final Vec3 mAxis;
    private final float mCos;
    private final float mSin;

    private final Vec3 tempCross;
    private final Vec3 tempV;

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

    // create a rotator which transforms a into b
    public static AxisAngleRotator capture(Vec3 a, Vec3 b) {
        Vec3 axis = a.cross(b);
        float axisNorm = axis.magnitude();
        if(axisNorm == 0) {
            return new AxisAngleRotator(new Vec3(0, 0, 1), 1, 0);
        }

        // with trig
        // double angle = Math.atan2(axisNorm, a.dot(b));
        // return new AxisAngleRotator(axis.scaleInverse(axisNorm), angle);

        // without trig
        float dot = a.dot(b);
        float divide = (float)(1 / Math.sqrt(axisNorm * axisNorm + dot * dot));
        float cos = dot * divide;
        float sin = axisNorm * divide;
        axis.scaleInverse(axisNorm);
        return new AxisAngleRotator(axis, cos, sin);
    }
}
