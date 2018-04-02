package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class CircleTest {

    @Test
    public void minimumBoundDegenerate() {
        ArrayList<Vec2> points = new ArrayList<>();
        try {
            Circle.minimumBound(points);
            fail();
        } catch (IllegalArgumentException ignored) {}
        Vec2 p1 = new Vec2(3, -4);
        points.add(p1);
        Circle c = Circle.minimumBound(points);
        assertTrue(c.radius == 0);
        assertTrue(c.center.equals(p1));
    }

    @Test
    public void minimumBoundTwo() {
        Vec2 p1 = new Vec2(3, -4);
        Vec2 p2 = new Vec2(-7, 2);
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(p1);
        points.add(p2);
        Circle c = Circle.minimumBound(points);
        assertTrue(c.radius * 2 == Vec2.distance(p1, p2));
        assertTrue(c.center.equals(Vec2.midpoint(p1, p2)));
    }

    @Test
    public void minimumBoundContains() {
        SuperRandom random = new SuperRandom(1234);
        Vec2 offset = new Vec2(1.23f, 4.56f);
        for(int round = 0; round < 10; round++) {
            ArrayList<Vec2> points = new ArrayList<>();
            for(int i = 0; i < 100; i++) {
                points.add(random.uniformUnit().scale(7).add(offset));
            }
            Circle c = Circle.minimumBound(points);
            float radius = c.radius;
            c.radius = radius * 1.000001f;
            for(Vec2 p : points) {
                assertTrue(c.contains(p));
            }
            c.radius = radius * 0.99999f;
            int outCount = 0;
            for(Vec2 p : points) {
                if(!c.contains(p)) {
                    outCount++;
                }
            }
            assertTrue(outCount >= 3);
        }
    }
}
