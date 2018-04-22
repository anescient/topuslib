package net.chaosworship.topuslib.geom2d;


import java.util.ArrayList;

public class Arc {

    private Circle mCircle;
    private double mMinRadians;
    private double mMaxRadians;

    // arc includes points on circle in angle range [aMin, aMax)
    public Arc(Circle circle, double minRadians, double maxRadians) {
        mCircle = circle;
        mMinRadians = minRadians;
        mMaxRadians = maxRadians;
    }

    public Arc(Circle circle) {
        this(circle, 0, 2 * Math.PI);
    }

    public double getStartRadians() {
        return mMinRadians;
    }

    public double getEndRadians() {
        return mMaxRadians;
    }

    public boolean isEmpty() {
        return mMaxRadians == mMinRadians;
    }

    public ArrayList<Vec2> getPointsAlong(int internalPoints) {
        int n = internalPoints + 2;
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            double a = mMinRadians + (double)i * (mMaxRadians - mMinRadians) / (n - 1);
            points.add(mCircle.getPointOnBound(a));
        }
        return points;
    }
}
