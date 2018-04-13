package net.chaosworship.topuslib.geom2d;

import static java.lang.Float.NaN;


// quick and dirty segment intersection
// many special cases are not handled
// do not use to control nuclear reactors
@SuppressWarnings("unused")
public class SegmentIntersection {

    private boolean mSegmentsIntersect;
    private float mAlongAB;
    private float mAlongCD;
    private Vec2 mIntersection;

    // test/calculate intersection of segment AB with segment CD
    public SegmentIntersection(Vec2 A, Vec2 B, Vec2 C, Vec2 D) {
        mAlongAB = NaN;
        mAlongCD = NaN;
        mIntersection = null;

        if(A.equals(B) || C.equals(D)) {
            mSegmentsIntersect = false;
        } else {
            // https://www.cdn.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
            boolean cwABC = clockwise(A, B, C);
            boolean cwABD = clockwise(A, B, D);
            boolean cwCDA = clockwise(C, D, A);
            boolean cwCDB = clockwise(C, D, B);
            mSegmentsIntersect = cwABC != cwABD && cwCDA != cwCDB;
        }

        if(mSegmentsIntersect) {
            // http://www.ahinson.com/algorithms_general/Sections/Geometry/ParametricLineIntersection.pdf
            float xDC = D.x - C.x;
            float xBA = B.x - A.x;
            float xCA = C.x - A.x;
            float yCA = C.y - A.y;
            float yBA = B.y - A.y;
            float yDC = D.y - C.y;
            float denom = xDC * yBA - xBA * yDC;
            if(denom == 0) {
                mSegmentsIntersect = false;
            } else {
                mAlongAB = (xDC * yCA - xCA * yDC) / denom;
                mAlongCD = (xBA * yCA - xCA * yBA) / denom;
                mIntersection = Vec2.mix(A, B, mAlongAB);
            }
        }
    }

    public static boolean connected(Vec2 A, Vec2 B, Vec2 C, Vec2 D) {
        return A.equals(C) || B.equals(C) || A.equals(D) || B.equals(D);
    }

    private static boolean clockwise(Vec2 P, Vec2 Q, Vec2 R) {
        return (Q.y - P.y) * (R.x - Q.x) - (Q.x - P.x) * (R.y - Q.y) > 0;
    }

    public boolean segmentsIntersect() {
        return mSegmentsIntersect;
    }

    public Vec2 getIntersection() {
        if(!mSegmentsIntersect) {
            throw new IllegalStateException();
        }
        return mIntersection;
    }

    public float getAlongAB() {
        if(!mSegmentsIntersect) {
            throw new IllegalStateException();
        }
        return mAlongAB;
    }

    public float getAlongCD() {
        if(!mSegmentsIntersect) {
            throw new IllegalStateException();
        }
        return mAlongCD;
    }
}
