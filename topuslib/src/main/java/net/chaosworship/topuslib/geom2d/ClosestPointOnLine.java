package net.chaosworship.topuslib.geom2d;


// relate a point to a line
@SuppressWarnings("SameParameterValue")
public class ClosestPointOnLine {

    private float mAlongAB; // 0 to 1 along segment AB
    private final Vec2 mOnAB; // point closest to Q

    // find point along line AB closest to Q
    // if segment, limit to the segment AB
    public ClosestPointOnLine(Vec2 Q, Vec2 A, Vec2 B, boolean segment) {

        float Ax2 = A.x * A.x;
        float Ay2 = A.y * A.y;
        float Bx2 = B.x * B.x;
        float By2 = B.y * B.y;

        // Jan.'18 from https://math.stackexchange.com/questions/1300484/distance-between-line-and-a-point?noredirect=1&lq=1
        float denom = Ax2 - 2 * A.x * B.x + Ay2 - 2 * A.y * B.y + Bx2 + By2;
        mAlongAB = 0;
        if(denom != 0) {
            mAlongAB = (Ax2 - A.x * (B.x + Q.x) + Ay2 - A.y * (B.y + Q.y) + B.x * Q.x + B.y * Q.y) / denom;
        }
        if(segment) {
            mAlongAB = mAlongAB < 0 ? 0 : mAlongAB;
            mAlongAB = mAlongAB > 1 ? 1 : mAlongAB;
        }
        mOnAB = B.difference(A).scale(mAlongAB).add(A);
    }

    public Vec2 getClosestOnAB() {
        return mOnAB;
    }

    public float getAlongAB() {
        return mAlongAB;
    }
}
