package net.chaosworship.topuslib.geom2d;


// relate points to a segment/line
// Jan.'18 from https://math.stackexchange.com/questions/1300484/distance-between-line-and-a-point?noredirect=1&lq=1
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class ClosestPointOnLine {

    private final Vec2 mA;
    private final Vec2 mB;
    private final float mAx2;
    private final float mAy2;
    private final float mDenom;
    private final Vec2 mClosest;

    // find point along line AB closest to Q
    // if segment, limit to the segment AB
    public ClosestPointOnLine(Vec2 A, Vec2 B) {
        mA = A;
        mB = B;
        mAx2 = A.x * A.x;
        mAy2 = A.y * A.y;
        mDenom = mAx2 - 2 * A.x * B.x + mAy2 - 2 * A.y * B.y + (B.x * B.x) + (B.y * B.y);
        mClosest = new Vec2();
    }

    public Vec2 getClosestOnAB(Vec2 p, boolean segment) {
        float along = getAlongAB(p);
        if(segment) {
            along = along < 0 ? 0 : along;
            along = along > 1 ? 1 : along;
        }
        mClosest.setDifference(mB, mA).scale(along).add(mA);
        return mClosest;
    }

    public float getAlongAB(Vec2 p) {
        if(mDenom == 0) {
            return 0;
        }
        return (mAx2 - mA.x * (mB.x + p.x) + mAy2 - mA.y * (mB.y + p.y) + mB.x * p.x + mB.y * p.y) / mDenom;
    }
}
