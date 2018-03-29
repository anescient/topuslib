package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;


public class TriangleTest {

    @Test
    public void contains() {
        Triangle t = new Triangle(new Vec2(1, 2), new Vec2(3, 4), new Vec2(5, -1));
        assertFalse(t.contains(new Vec2(1.999999f, 3)));
        assertTrue(t.contains(new Vec2(2.0000001f, 3)));
        assertFalse(t.contains(new Vec2(0, 0)));
        assertFalse(t.contains(new Vec2(1, 3)));
        assertFalse(t.contains(new Vec2(3, 0)));
        assertFalse(t.contains(new Vec2(4, -1)));
        assertFalse(t.contains(new Vec2(4, 2)));
        assertFalse(t.contains(new Vec2(4, 5)));
        assertFalse(t.contains(new Vec2(5, -2)));
        assertTrue(t.contains(new Vec2(3, 1)));
        assertTrue(t.contains(new Vec2(2, 2)));
        assertTrue(t.contains(new Vec2(3, 2)));
        assertTrue(t.contains(new Vec2(4, 0)));
        assertTrue(t.contains(new Vec2(4, 1)));
        assertTrue(t.contains(new Vec2(3, 3)));
    }

    // Triangle's "contains" method is inconsistent when query point lies on an edge.
    // This test is here to catch any change in behavior, not to verify correctness.
    @Test
    public void containsOnEdgeIsBroken() {
        Vec2 a = new Vec2(0, 0);
        Vec2 b = new Vec2(3, 3);
        Vec2 c = new Vec2(7, 1);
        Vec2 m_ab = Vec2.midpoint(a, b);
        Vec2 m_bc = Vec2.midpoint(b, c);

        Triangle abc = new Triangle(a, b, c);
        Triangle acb = new Triangle(a, c, b);
        Triangle bac = new Triangle(b, a, c);
        Triangle bca = new Triangle(b, c, a);
        Triangle cab = new Triangle(c, a, b);
        Triangle cba = new Triangle(c, b, a);

        assertTrue(abc.contains(m_ab));
        assertTrue(acb.contains(m_ab));
        assertFalse(bac.contains(m_ab));
        assertFalse(bca.contains(m_ab));
        assertTrue(cab.contains(m_ab));
        assertFalse(cba.contains(m_ab));

        assertTrue(abc.contains(m_bc));
        assertFalse(acb.contains(m_bc));
        assertTrue(bac.contains(m_bc));
        assertTrue(bca.contains(m_bc));
        assertFalse(cab.contains(m_bc));
        assertFalse(cba.contains(m_bc));
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
                assertFalse(t.contains(p));
                p.setUnit(pointRotation).scale(triangleRadius * 0.28f).add(origin);
                assertTrue(t.contains(p));
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
        assertTrue(t.closestPointOnBound(new Vec2(-1, -1)).getClosestOnAB().epsilonEquals(t.pointA, e));
        assertTrue(t.closestPointOnBound(new Vec2(2, 4)).getClosestOnAB().epsilonEquals(t.pointB, e));
        assertTrue(t.closestPointOnBound(new Vec2(10, -1)).getClosestOnAB().epsilonEquals(t.pointC, e));
        assertTrue(t.closestPointOnBound(new Vec2(4, -1)).getClosestOnAB().epsilonEquals(new Vec2(4, 0), e));
        Vec2 midAB = Vec2.midpoint(t.pointA, t.pointB);
        Vec2 midBC = Vec2.midpoint(t.pointB, t.pointC);
        Vec2 midCA = Vec2.midpoint(t.pointC, t.pointA);
        assertTrue(t.closestPointOnBound(midAB).getClosestOnAB().epsilonEquals(midAB, e));
        assertTrue(t.closestPointOnBound(midBC).getClosestOnAB().epsilonEquals(midBC, e));
        assertTrue(t.closestPointOnBound(midCA).getClosestOnAB().epsilonEquals(midCA, e));

        t.pointA.set(0, 0);
        t.pointB.set(2, 3);
        t.pointC.set(4, 0);
        assertTrue(t.closestPointOnBound(new Vec2(2, 1)).getClosestOnAB().epsilonEquals(new Vec2(2, 0), e));
    }

    private static boolean softEquals(float a, float b, float delta) {
        return Math.abs(a - b) < delta;
    }
}
