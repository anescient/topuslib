package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.*;


public class CircumcircleTest {

    @Test
    public void contains() {
        Random random = new Random(1234);
        for(int i = 0; i < 10; i++) {
            Vec2 center = Vec2.unit(random.nextDouble() * Math.PI * 2).scale(random.nextFloat() * 50);
            float radius = random.nextFloat() * 20;
            Vec2 a = Vec2.unit(random.nextDouble() * Math.PI * 2).scale(radius).add(center);
            Vec2 b = Vec2.unit(random.nextDouble() * Math.PI * 2).scale(radius).add(center);
            Vec2 c = Vec2.unit(random.nextDouble() * Math.PI * 2).scale(radius).add(center);
            for(int j = 0; j < 20; j++) {
                Vec2 p = Vec2.unit(random.nextDouble() * Math.PI * 2).scale(random.nextFloat() * radius * 0.99f).add(center);
                assertTrue(Circumcircle.contains(a, b, c, p));
                assertTrue(Circumcircle.contains(a, c, b, p));
            }
            for(int j = 0; j < 20; j++) {
                Vec2 p = Vec2.unit(random.nextDouble() * Math.PI * 2).scale((1.01f + random.nextFloat() * 20) * radius).add(center);
                assertFalse(Circumcircle.contains(a, b, c, p));
                assertFalse(Circumcircle.contains(a, c, b, p));
            }
        }
    }

    @Test
    public void toCircle() {
        SuperRandom random = new SuperRandom(1234);
        for(int i = 0; i < 100; i++) {
            Vec2 center = random.uniformUnit().scale(random.nextFloat() * 10);
            float radius = random.nextFloat() * 3;
            Circle circle = new Circle(center, radius);
            List<Vec2> points = circle.getBoundPoints(7);
            Circle recircle = Circumcircle.toCircle(
                    points.get(0), points.get(2), points.get(5));
            assertTrue(probablyEqual(center.x, recircle.center.x));
            assertTrue(probablyEqual(center.y, recircle.center.y));
            assertTrue(probablyEqual(radius, recircle.radius));
        }
    }

    private static boolean probablyEqual(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }
}
