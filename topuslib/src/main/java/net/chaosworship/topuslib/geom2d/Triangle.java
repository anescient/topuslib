package net.chaosworship.topuslib.geom2d;


public class Triangle implements SolidShape {

    public Vec2 pointA;
    public Vec2 pointB;
    public Vec2 pointC;

    @SuppressWarnings("WeakerAccess")
    public Triangle() {
        pointA = new Vec2();
        pointB = new Vec2();
        pointC = new Vec2();
    }

    public Triangle(Vec2 a, Vec2 b, Vec2 c) {
        this();
        set(a, b, c);
    }

    @SuppressWarnings("unused")
    public Triangle(Triangle source) {
        this(source.pointA, source.pointB, source.pointC);
    }

    public void set(Vec2 a, Vec2 b, Vec2 c) {
        pointA = a;
        pointB = b;
        pointC = c;
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        // this isn't guaranteed correct if either is degenerate
        Triangle rhsTriangle = (Triangle)rhs;
        return rhsTriangle.contains(pointA) &&
                rhsTriangle.contains(pointB) &&
                rhsTriangle.contains(pointC);
    }

    public Vec2 closestPointOnBound(Vec2 point) {
        return closestPointOnBound(pointA, pointB, pointC, point);
    }

    public float distanceSquaredFromBound(Vec2 point) {
        return distanceSquaredFromBound(pointA, pointB, pointC, point);
    }

    @Override
    public boolean contains(Vec2 point) {
        boolean b1 = point.inHalfPlane(pointA, pointB);
        boolean b2 = point.inHalfPlane(pointB, pointC);
        boolean b3 = point.inHalfPlane(pointC, pointA);
        return (b1 == b2) && (b2 == b3);
    }

    @Override
    public float area() {
        return area(pointA, pointB, pointC);
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return Rectangle.bound(pointA, pointB, pointC);
    }

    public boolean isDegenerate() {
        return pointA.equals(pointB) || pointB.equals(pointC) || pointC.equals(pointA);
    }

    @SuppressWarnings("WeakerAccess")
    public static Vec2 closestPointOnBound(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        ClosestPointOnLine closestAB = new ClosestPointOnLine(a, b);
        ClosestPointOnLine closestBC = new ClosestPointOnLine(b, c);
        ClosestPointOnLine closestCA = new ClosestPointOnLine(c, a);
        float dAB = Vec2.distanceSq(p, closestAB.getClosestOnAB(p, true));
        float dBC = Vec2.distanceSq(p, closestBC.getClosestOnAB(p, true));
        float dCA = Vec2.distanceSq(p, closestCA.getClosestOnAB(p, true));
        ClosestPointOnLine closest;
        if(dAB < dBC) {
            closest = dAB < dCA ? closestAB : closestCA;
        } else {
            closest = dBC < dCA ? closestBC : closestCA;
        }
        return closest.getClosestOnAB(p, true);
    }

    @SuppressWarnings("WeakerAccess")
    public static float distanceSquaredFromBound(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        Vec2 closest = closestPointOnBound(a, b, c, p);
        return closest.subtract(p).magnitudeSq();
    }

    @SuppressWarnings("WeakerAccess")
    public static float area(Vec2 a, Vec2 b, Vec2 c) {
        return Math.abs(a.y * (c.x - b.x) + b.y * (a.x - c.x) + c.y * (b.x - a.x)) * 0.5f;
    }
}
