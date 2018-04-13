package net.chaosworship.topuslib.geom2d;


public class Triangle {

    public Vec2 pointA;
    public Vec2 pointB;
    public Vec2 pointC;

    public Triangle() {
        pointA = new Vec2();
        pointB = new Vec2();
        pointC = new Vec2();
    }

    public Triangle(Vec2 a, Vec2 b, Vec2 c) {
        this();
        set(a, b, c);
    }

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

    public ClosestPointOnLine closestPointOnBound(Vec2 point) {
        return closestPointOnBound(pointA, pointB, pointC, point);
    }

    public float distanceSquaredFromBound(Vec2 point) {
        return distanceSquaredFromBound(pointA, pointB, pointC, point);
    }

    public boolean contains(Vec2 point) {
        return contains(pointA, pointB, pointC, point);
    }

    public boolean isDegenerate() {
        return pointA.equals(pointB) || pointB.equals(pointC) || pointC.equals(pointA);
    }

    public float area() {
        return area(pointA, pointB, pointC);
    }

    @SuppressWarnings("WeakerAccess")
    public static ClosestPointOnLine closestPointOnBound(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        ClosestPointOnLine closestAB = new ClosestPointOnLine(p, a, b, true);
        ClosestPointOnLine closestBC = new ClosestPointOnLine(p, b, c, true);
        ClosestPointOnLine closestCA = new ClosestPointOnLine(p, c, a, true);
        float dAB = Vec2.distanceSq(p, closestAB.getClosestOnAB());
        float dBC = Vec2.distanceSq(p, closestBC.getClosestOnAB());
        float dCA = Vec2.distanceSq(p, closestCA.getClosestOnAB());
        ClosestPointOnLine closest;
        if(dAB < dBC) {
            closest = dAB < dCA ? closestAB : closestCA;
        } else {
            closest = dBC < dCA ? closestBC : closestCA;
        }
        return closest;
    }

    public static float distanceSquaredFromBound(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        Vec2 closest = closestPointOnBound(a, b, c, p).getClosestOnAB();
        return closest.subtract(p).magnitudeSq();
    }

    private static boolean contains(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        boolean b1 = p.inHalfPlane(a, b);
        boolean b2 = p.inHalfPlane(b, c);
        boolean b3 = p.inHalfPlane(c, a);
        return (b1 == b2) && (b2 == b3);
    }

    @SuppressWarnings("WeakerAccess")
    public static float area(Vec2 a, Vec2 b, Vec2 c) {
        return Math.abs(a.y * (c.x - b.x) + b.y * (a.x - c.x) + c.y * (b.x - a.x)) * 0.5f;
    }
}
