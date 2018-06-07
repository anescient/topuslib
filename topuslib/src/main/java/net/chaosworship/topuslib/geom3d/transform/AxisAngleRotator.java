package net.chaosworship.topuslib.geom3d.transform;

import net.chaosworship.topuslib.geom3d.Vec3;


public class AxisAngleRotator {

    private final Vec3 mAxis;
    private final float mCos;
    private final float mSin;

    public AxisAngleRotator(Vec3 unitAxis, double angle) {
        mAxis = unitAxis;
        mCos = (float)Math.cos(angle);
        mSin = (float)Math.sin(angle);
    }

    private AxisAngleRotator(Vec3 unitAxis, float cos, float sin) {
        mAxis = unitAxis;
        mCos = cos;
        mSin = sin;
    }

    public Vec3 rotated(Vec3 v) {
        // Rodrigues' rotation formula
        return new Vec3()
            .addScaled(v, mCos)
            .addScaled(mAxis.cross(v), mSin)
            .addScaled(mAxis, (mAxis.dot(v) * (1 - mCos)));
    }

    // create a rotator which transforms a into b
    public static AxisAngleRotator capture(Vec3 a, Vec3 b) {
        Vec3 cross = a.cross(b);
        if(cross.isZero()) {
            return new AxisAngleRotator(new Vec3(0, 0, 1), 1, 0);
        }
        float crossNorm = cross.magnitude();

        // with trig
        // double angle = Math.atan2(crossNorm, a.dot(b));
        // return new AxisAngleRotator(cross.scaleInverse(crossNorm), angle);

        // without trig
        float dot = a.dot(b);
        float divide = (float)(1 / Math.sqrt(crossNorm * crossNorm + dot * dot));
        return new AxisAngleRotator(cross.scaleInverse(crossNorm), dot * divide, crossNorm * divide);
    }
}
