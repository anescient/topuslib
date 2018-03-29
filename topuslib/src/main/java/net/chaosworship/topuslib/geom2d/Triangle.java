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

    public boolean contains(Vec2 point) {
        float APx = point.x - pointA.x;
        float APy = point.y - pointA.y;
        float ABx = pointB.x - pointA.x;
        float ABy = pointB.y - pointA.y;
        float ACx = pointC.x - pointA.x;
        float ACy = pointC.y - pointA.y;
        float BCx = pointC.x - pointB.x;
        float BCy = pointC.y - pointB.y;
        float BPx = point.x - pointB.x;
        float BPy = point.y - pointB.y;
        boolean PAB = ABx * APy - ABy * APx > 0;
        return (ACx * APy - ACy * APx > 0 != PAB) && (BCx * BPy - BCy * BPx > 0 == PAB);
    }

    public ClosestPointOnLine closestPointOnBound(Vec2 point) {
        ClosestPointOnLine closestAB = new ClosestPointOnLine(point, pointA, pointB, true);
        ClosestPointOnLine closestBC = new ClosestPointOnLine(point, pointB, pointC, true);
        ClosestPointOnLine closestCA = new ClosestPointOnLine(point, pointC, pointA, true);
        float dAB = Vec2.distanceSq(point, closestAB.getClosestOnAB());
        float dBC = Vec2.distanceSq(point, closestBC.getClosestOnAB());
        float dCA = Vec2.distanceSq(point, closestCA.getClosestOnAB());
        ClosestPointOnLine closest;
        if(dAB < dBC) {
            closest = dAB < dCA ? closestAB : closestCA;
        } else {
            closest = dBC < dCA ? closestBC : closestCA;
        }
        return closest;
    }

    public float area() {
        return Math.abs(
                pointA.y * (pointC.x - pointB.x)
                + pointB.y * (pointA.x - pointC.x)
                + pointC.y * (pointB.x - pointA.x)) * 0.5f;
    }
}
