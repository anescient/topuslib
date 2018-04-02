package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Vec2;

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
}
