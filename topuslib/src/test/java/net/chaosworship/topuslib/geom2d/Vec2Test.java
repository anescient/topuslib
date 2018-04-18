package net.chaosworship.topuslib.geom2d;

import org.junit.Test;

import static junit.framework.Assert.*;


public class Vec2Test {

    @Test
    public void atan2UnitMatch() {
        for(float a = 0; a < 7; a += 0.123f) {
            Vec2 v1 = Vec2.unit(a);
            Vec2 v2 = Vec2.unit(v1.atan2());
            assertTrue(probablyEqual(v1, v2));
        }
    }

    private static boolean probablyEqual(Vec2 a, Vec2 b) {
        return probablyEqual(a.x, b.x) && probablyEqual(a.y, b.y);
    }

    private static boolean probablyEqual(float a, float b) {
        return Math.abs(a - b) < 0.000001f;
    }

    @Test
    public void inHalfPlane() {
        Vec2 a = new Vec2(0, 0);
        Vec2 b = new Vec2(3, 2);

        assertFalse(doubleInHalfPlane(a, a, b));
        assertFalse(doubleInHalfPlane(b, a, b));
        assertFalse(doubleInHalfPlane(a, b, a));
        assertFalse(doubleInHalfPlane(b, b, a));
        assertFalse(doubleInHalfPlane(new Vec2(6, 4), a, b));

        assertTrue(doubleInHalfPlane(new Vec2(2, 1), a, b));
        assertFalse(doubleInHalfPlane(new Vec2(2, 1), b, a));

        assertTrue(doubleInHalfPlane(new Vec2(-2, -2), a, b));
        assertFalse(doubleInHalfPlane(new Vec2(-2, -2), b, a));
    }

    private static boolean doubleInHalfPlane(Vec2 p, Vec2 a, Vec2 b) {
        boolean in = Vec2.inHalfPlane(p, a, b);
        assertTrue(in == p.inHalfPlane(a, b));
        return in;
    }
}
