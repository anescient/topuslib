package net.chaosworship.topuslib.geom2d;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;


public class TriangleTest {

    @Test
    public void contains() {
        Triangle t = new Triangle(new Vec2(1, 2), new Vec2(3, 4), new Vec2(5, -1));
        assertFalse(checkedContains(t, new Vec2(1.999999f, 3)));
        assertTrue(checkedContains(t, new Vec2(2.000001f, 3)));
        assertFalse(checkedContains(t, new Vec2(0, 0)));
        assertFalse(checkedContains(t, new Vec2(1, 3)));
        assertFalse(checkedContains(t, new Vec2(3, 0)));
        assertFalse(checkedContains(t, new Vec2(4, -1)));
        assertFalse(checkedContains(t, new Vec2(4, 2)));
        assertFalse(checkedContains(t, new Vec2(4, 5)));
        assertFalse(checkedContains(t, new Vec2(5, -2)));
        assertTrue(checkedContains(t, new Vec2(3, 1)));
        assertTrue(checkedContains(t, new Vec2(2, 2)));
        assertTrue(checkedContains(t, new Vec2(3, 2)));
        assertTrue(checkedContains(t, new Vec2(4, 0)));
        assertTrue(checkedContains(t, new Vec2(4, 1)));
        assertTrue(checkedContains(t, new Vec2(3, 3)));
    }

    @Test
    public void containsOnEdge() {
        Vec2 a = new Vec2(0, 0);
        Vec2 b = new Vec2(3, 3);
        Vec2 c = new Vec2(7, 1);
        Vec2 m_ab = Vec2.midpoint(a, b);
        Vec2 m_bc = Vec2.midpoint(b, c);

        Triangle cw_abc = new Triangle(a, b, c);
        Triangle cw_bca = new Triangle(b, c, a);
        Triangle cw_cab = new Triangle(c, a, b);
        Triangle ccw_acb = new Triangle(a, c, b);
        Triangle ccw_bac = new Triangle(b, a, c);
        Triangle ccw_cba = new Triangle(c, b, a);

        assertFalse(cw_abc.contains(m_ab));
        assertFalse(cw_bca.contains(m_ab));
        assertFalse(cw_cab.contains(m_ab));
        assertTrue(ccw_acb.contains(m_ab));
        assertTrue(ccw_bac.contains(m_ab));
        assertTrue(ccw_cba.contains(m_ab));

        assertFalse(cw_abc.contains(m_bc));
        assertFalse(cw_bca.contains(m_bc));
        assertFalse(cw_cab.contains(m_bc));
        assertTrue(ccw_acb.contains(m_bc));
        assertTrue(ccw_bac.contains(m_bc));
        assertTrue(ccw_cba.contains(m_bc));
    }

    @Test
    public void containsRotateOffset() {
        Triangle t = new Triangle();
        Random random = new Random(1234);
        Vec2 p = new Vec2();
        Vec2 origin = new Vec2();
        for(int i = 0; i < 100; i++) {
            origin.set(100 * (random.nextFloat() - 0.5f), 100 * (random.nextFloat() - 0.5f));
            double triangleRotation = random.nextDouble() * 2 * Math.PI;
            float triangleRadius = 1 + 9 * random.nextFloat();
            t.pointA.setUnit(triangleRotation).scale(triangleRadius).add(origin);
            t.pointB.setUnit(triangleRotation + 2 * Math.PI / 3).scale(triangleRadius).add(origin);
            t.pointC.setUnit(triangleRotation + 4 * Math.PI / 3).scale(triangleRadius).add(origin);
            for(double pointRotation = 0; pointRotation < 2 * Math.PI; pointRotation += 0.03) {
                p.setUnit(pointRotation).scale(triangleRadius * 1.001f).add(origin);
                assertFalse(checkedContains(t, p));
                p.setUnit(pointRotation).scale(triangleRadius * 0.28f).add(origin);
                assertTrue(checkedContains(t, p));
            }
        }
    }

    @Test
    public void area() {
        Triangle t = new Triangle();

        t.pointA.set(0, 0);
        t.pointB.set(1, 0);
        t.pointC.set(1, 1);
        assertTrue(softEquals(t.area(), 0.5f, 0.000001f));

        t.pointB.set(0, 0);
        assertTrue(t.isDegenerate());
        assertTrue(t.area() == 0);
        t.pointB.set(1, 1);
        assertTrue(t.isDegenerate());
        assertTrue(t.area() == 0);

        Random random = new Random(1234);
        t.pointA.set(0, 0);
        for(int i = 0; i < 100; i++) {
            float width = 10 * random.nextFloat();
            float height = 10 * random.nextFloat();
            t.pointC.set(width, 0);
            t.pointB.set(random.nextFloat() * width, height);
            assertTrue(softEquals(t.area(), width * height / 2, 0.001f));
        }
    }

    @Test
    public void areaEquilateralRotateOffset() {
        Triangle t = new Triangle();
        Random random = new Random(1234);
        Vec2 origin = new Vec2();
        float unitArea = (float)(Math.sqrt(3) / 4);
        for(int i = 0; i < 100; i++) {
            origin.set(100 * (random.nextFloat() - 0.5f), 100 * (random.nextFloat() - 0.5f));
            double triangleRotation = random.nextDouble() * 2 * Math.PI;
            float triangleRadius = 1 + 9 * random.nextFloat();
            t.pointA.setUnit(triangleRotation).scale(triangleRadius).add(origin);
            t.pointB.setUnit(triangleRotation + 2 * Math.PI / 3).scale(triangleRadius).add(origin);
            t.pointC.setUnit(triangleRotation + 4 * Math.PI / 3).scale(triangleRadius).add(origin);
            float sideLength = Vec2.distance(t.pointA, t.pointB);
            float expectedArea = unitArea * sideLength * sideLength;
            assertTrue(softEquals(expectedArea, t.area(), 0.001f));
        }
    }

    @Test
    public void closestOnBound() {
        final float e = 0.000001f;
        Triangle t = new Triangle();

        t.pointA.set(0, 0);
        t.pointB.set(2, 3);
        t.pointC.set(9, 0);
        assertTrue(t.closestPointOnBound(new Vec2(-1, -1)).epsilonEquals(t.pointA, e));
        assertTrue(t.closestPointOnBound(new Vec2(2, 4)).epsilonEquals(t.pointB, e));
        assertTrue(t.closestPointOnBound(new Vec2(10, -1)).epsilonEquals(t.pointC, e));
        assertTrue(t.closestPointOnBound(new Vec2(4, -1)).epsilonEquals(new Vec2(4, 0), e));
        Vec2 midAB = Vec2.midpoint(t.pointA, t.pointB);
        Vec2 midBC = Vec2.midpoint(t.pointB, t.pointC);
        Vec2 midCA = Vec2.midpoint(t.pointC, t.pointA);
        assertTrue(t.closestPointOnBound(midAB).epsilonEquals(midAB, e));
        assertTrue(t.closestPointOnBound(midBC).epsilonEquals(midBC, e));
        assertTrue(t.closestPointOnBound(midCA).epsilonEquals(midCA, e));

        t.pointA.set(0, 0);
        t.pointB.set(2, 3);
        t.pointC.set(4, 0);
        assertTrue(t.closestPointOnBound(new Vec2(2, 1)).epsilonEquals(new Vec2(2, 0), e));
    }

    private static boolean softEquals(float a, float b, float delta) {
        return Math.abs(a - b) < delta;
    }

    private static boolean checkedContains(Triangle t, Vec2 p) {
        boolean contains = t.contains(p);
        boolean shouldContain = containsReferenceBarycentric(t, p);
        assertTrue(contains == shouldContain);
        return contains;
    }

    private static boolean containsReferenceBarycentric(Triangle tt, Vec2 p)
    {
        Vec2 p0 = tt.pointA;
        Vec2 p1 = tt.pointB;
        Vec2 p2 = tt.pointC;
        float s = p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x) * p.y;
        float t = p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y;

        if ((s < 0) != (t < 0))
            return false;

        float A = -p1.y * p2.x + p0.y * (p2.x - p1.x) + p0.x * (p1.y - p2.y) + p1.x * p2.y;
        if (A < 0.0)
        {
            s = -s;
            t = -t;
            A = -A;
        }
        return s > 0 && t > 0 && (s + t) <= A;
    }

    private static boolean containsReferenceCross(Triangle t, Vec2 p) {
        Vec2 pa = t.pointA.difference(p);
        Vec2 pb = t.pointB.difference(p);
        Vec2 pc = t.pointC.difference(p);
        Vec2 ab = t.pointB.difference(t.pointA);
        Vec2 bc = t.pointC.difference(t.pointB);
        Vec2 ca = t.pointA.difference(t.pointC);
        float abcross = ab.cross(pa);
        float bccross = bc.cross(pb);
        float cacross = ca.cross(pc);
        return abcross < 0 == bccross < 0 && bccross < 0 == cacross < 0;
    }
}
