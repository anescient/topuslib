package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.triangulation.DelaunayTriangulation;
import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class DelaunayTest {

    @Test
    public void isDelaunay() {
        SuperRandom random = new SuperRandom(1234);
        ArrayList<Vec2> points = new ArrayList<>();
        for(int pointCount = 3; pointCount < 50; pointCount++) {
            points.clear();
            for(int i = 0; i < pointCount; i++) {
                points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            }
            ArrayList<Triangle> triangles = new DelaunayTriangulation(points).getTriangles();
            assertTrue(triangles.size() >= pointCount - 2);
            for(Triangle t : triangles) {
                Circle c = Circumcircle.toCircle(t);
                c.radius *= 0.99999f;
                for(Vec2 p : points) {
                    assertFalse(c.contains(p));
                }
            }
        }
    }
}
