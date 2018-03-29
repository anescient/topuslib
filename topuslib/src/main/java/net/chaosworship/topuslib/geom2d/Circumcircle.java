package net.chaosworship.topuslib.geom2d;


public class Circumcircle {

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
