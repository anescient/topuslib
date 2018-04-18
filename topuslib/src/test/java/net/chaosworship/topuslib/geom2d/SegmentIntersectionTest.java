package net.chaosworship.topuslib.geom2d;

import org.junit.Test;

import static junit.framework.Assert.*;


public class SegmentIntersectionTest {

    @Test
    public void intersectionPosition() {
        SegmentIntersection intersection;
        Vec2 a = new Vec2(0, 0);
        Vec2 b = new Vec2(3, 3);
        Vec2 c = new Vec2(2, -1);
        Vec2 d = new Vec2(1, 4);
        Vec2 p = new Vec2(1.5f, 1.5f);
        intersection = new SegmentIntersection(a, b, c, d);
        assertTrue(intersection.segmentsIntersect());
        assertTrue(intersection.getIntersection().epsilonEquals(p, 0.00000001f));
        intersection = new SegmentIntersection(c, d, a, b);
        assertTrue(intersection.segmentsIntersect());
        assertTrue(intersection.getIntersection().epsilonEquals(p, 0.00000001f));
        intersection = new SegmentIntersection(b, a, d, c);
        assertTrue(intersection.segmentsIntersect());
        assertTrue(intersection.getIntersection().epsilonEquals(p, 0.00000001f));
    }

    @Test
    public void noIntersection() {
        SegmentIntersection intersection;
        Vec2 a = new Vec2(0, 0);
        Vec2 b = new Vec2(3, 3);
        Vec2 c = new Vec2(2, -1);
        Vec2 d = new Vec2(1, 4);
        intersection = new SegmentIntersection(a, d, b, c);
        assertFalse(intersection.segmentsIntersect());
        intersection = new SegmentIntersection(a, c, d, b);
        assertFalse(intersection.segmentsIntersect());
        try {
            intersection.getIntersection();
            fail();
        } catch(IllegalStateException ignored) {}
        try {
            intersection.getAlongAB();
            fail();
        } catch(IllegalStateException ignored) {}
        try {
            intersection.getAlongCD();
            fail();
        } catch(IllegalStateException ignored) {}
    }

    @Test
    public void intersectsRotated() {
        SegmentIntersection intersection;
        Vec2 center = new Vec2(7.1234f, -13.5678f);
        Vec2 a = new Vec2();
        Vec2 b = new Vec2();
        Vec2 c = new Vec2();
        Vec2 d = new Vec2();
        float quarter = (float)Math.PI / 2;
        for(int i = 0; i < 360; i++) {
            float theta = (float)(Math.PI * 2 * i / 360.0);
            a.setUnit(theta).add(center);
            b.setUnit(theta + quarter).add(center);
            c.setUnit(theta + 2 * quarter).add(center);
            d.setUnit(theta + 3 * quarter).add(center);
            intersection = new SegmentIntersection(a, c, b, d);
            assertTrue(intersection.getIntersection().epsilonEquals(center, 0.000001f));
            assertTrue(probablyEqual(intersection.getAlongAB(), 0.5));
            assertTrue(probablyEqual(intersection.getAlongCD(), 0.5));
        }
    }

    @Test
    public void along() {
        SegmentIntersection intersection;
        Vec2 center = new Vec2(7.1234f, -13.5678f);
        Vec2 a = new Vec2(0, 0).add(center);
        Vec2 b = new Vec2(3, 3).add(center);
        Vec2 c = new Vec2(3, 0).add(center);
        Vec2 d = new Vec2(1, 2).add(center);
        intersection = new SegmentIntersection(a, b, c, d);
        assertTrue(probablyEqual(intersection.getAlongAB(), 0.5));
        assertTrue(probablyEqual(intersection.getAlongCD(), 0.75));
        intersection = new SegmentIntersection(b, a, d, c);
        assertTrue(probablyEqual(intersection.getAlongAB(), 0.5));
        assertTrue(probablyEqual(intersection.getAlongCD(), 0.25));
    }

    private static boolean probablyEqual(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }
}
