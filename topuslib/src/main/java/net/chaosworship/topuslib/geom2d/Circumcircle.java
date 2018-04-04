package net.chaosworship.topuslib.geom2d;


public class Circumcircle {

    public static Circle toCircle(Vec2 a, Vec2 b) {
        Vec2 center = Vec2.midpoint(a, b);
        float radius = Vec2.distance(center, a);
        return new Circle(center, radius);
    }

    public static Circle toCircle(Vec2 a, Vec2 b, Vec2 c) {
        Vec2 center;
        float radius;

        float Bx = b.x - a.x;
        float By = b.y - a.y;
        float Cx = c.x - a.x;
        float Cy = c.y - a.y;
        float D = 2 * (Bx * Cy - By * Cx);
        if(D == 0) {
            center = new Vec2(a);
            radius = 0;
        } else {
            float Bx2 = Bx * Bx;
            float By2 = By * By;
            float Cx2 = Cx * Cx;
            float Cy2 = Cy * Cy;
            center = new Vec2(
                    (Cy * (Bx2 + By2) - By * (Cx2 + Cy2)) / D,
                    (Bx * (Cx2 + Cy2) - Cx * (Bx2 + By2)) / D);
            radius = center.magnitude();
            center.add(a);
        }

        return new Circle(center, radius);
    }

    public static boolean contains(Triangle abc, Vec2 p) {
        return contains(abc.pointA, abc.pointB, abc.pointC, p);
    }

    // test if circumcircle abc contains p
    public static boolean contains(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
        boolean ccw = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y) > 0;
        if(!ccw) {
            Vec2 aa = a;
            a = b;
            b = aa;
        }

        float pax = a.x - p.x;
        float pay = a.y - p.y;
        float pbx = b.x - p.x;
        float pby = b.y - p.y;
        float pcx = c.x - p.x;
        float pcy = c.y - p.y;
        return ((pax * pax + pay * pay) * (pbx * pcy - pcx * pby) -
                (pbx * pbx + pby * pby) * (pax * pcy - pcx * pay) +
                (pcx * pcx + pcy * pcy) * (pax * pby - pbx * pay)) > 0;
    }
}
