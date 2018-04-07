package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.mesh.DelaunayTriangulator;
import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.*;


public class DelaunayTest {

    @Test
    public void isDelaunay() {
        SuperRandom random = new SuperRandom(1234);
        ArrayList<Vec2> points = new ArrayList<>();
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        for(int pointCount = 3; pointCount < 50; pointCount++) {
            points.clear();
            for(int i = 0; i < pointCount; i++) {
                points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            }
            triangulator.triangulate(points);
            assertDelaunny(points, triangulator.getTriangles());
        }
    }

    private static void assertDelaunny(Collection<Vec2> points, Collection<Triangle> triangles) {
        assertTrue(triangles.size() >= points.size() - 2);
        for(Triangle t : triangles) {
            for(Triangle tt : triangles) {
                assertTrue(t == tt || !t.equals(tt));
            }
            Circle c = Circumcircle.toCircle(t);
            c.radius *= 0.99999f;
            for(Vec2 p : points) {
                assertFalse(c.contains(p));
            }
        }
    }
}
