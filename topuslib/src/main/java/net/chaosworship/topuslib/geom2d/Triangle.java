package net.chaosworship.topuslib.geom2d;


public class Triangle {

    public final Vec2 pointA;
    public final Vec2 pointB;
    public final Vec2 pointC;

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
        pointA.set(a);
        pointB.set(b);
        pointC.set(c);
    }

    public ClosestPointOnLine closestPointOnBound(Vec2 point) {
        return closestPointOnBound(pointA, pointB, pointC, point);
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

    public static boolean contains(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        float APx = p.x - a.x;
        float APy = p.y - a.y;
        float ABx = b.x - a.x;
        float ABy = b.y - a.y;
        float ACx = c.x - a.x;
        float ACy = c.y - a.y;
        float BCx = c.x - b.x;
        float BCy = c.y - b.y;
        float BPx = p.x - b.x;
        float BPy = p.y - b.y;
        boolean PAB = ABx * APy - ABy * APx > 0;
        return (ACx * APy - ACy * APx > 0 != PAB) && (BCx * BPy - BCy * BPx > 0 == PAB);
    }

    @SuppressWarnings("WeakerAccess")
    public static float area(Vec2 a, Vec2 b, Vec2 c) {
        return Math.abs(a.y * (c.x - b.x) + b.y * (a.x - c.x) + c.y * (b.x - a.x)) * 0.5f;
    }
}
