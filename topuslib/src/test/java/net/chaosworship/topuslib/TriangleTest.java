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
}
