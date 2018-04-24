package net.chaosworship.topuslib.geom2d;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class RectangleTest {

    @Test
    public void degenerate() {
        float x = (float)Math.PI;
        float y = -x;
        Rectangle r = new Rectangle(x, y, x, y);
        assertTrue(r.width() == 0);
        assertTrue(r.height() == 0);
        assertTrue(r.containsClosed(new Vec2(x, y)));
        assertFalse(r.containsOpen(new Vec2(x, y)));
    }

    @Test
    public void contains() {
        Rectangle r = new Rectangle(-1, -2, 3, 4);
        Vec2 p = new Vec2();

        p.set(0, 0);
        assertTrue(r.containsOpen(p));
        assertTrue(r.containsClosed(p));

        p.set(-1, -2);
        assertTrue(r.containsClosed(p));
        assertFalse(r.containsOpen(p));

        p.set(3, 4);
        assertTrue(r.containsClosed(p));
        assertFalse(r.containsOpen(p));

        p.set(-1, -3);
        assertFalse(r.containsClosed(p));
        assertFalse(r.containsOpen(p));
    }

    @Test
    public void containsCircle() {
        Rectangle r = new Rectangle(0, 0, 4, 2);
        Circle c = new Circle();
        c.center.setZero();
        c.radius = 0;
        assertTrue(r.contains(c));
        c.radius = 0.000001f;
        assertFalse(r.contains(c));
        c.center.set(2, 1);
        c.radius = 1;
        assertTrue(r.contains(c));
        c.radius = 1.000001f;
        assertFalse(r.contains(c));
        c.center.set(1, 1);
        c.radius = 1;
        assertTrue(r.contains(c));
        c.center.x -= 0.000001f;
        assertFalse(r.contains(c));
        c.center.set(3, 1);
        assertTrue(r.contains(c));
        c.center.x += 0.000001f;
        assertFalse(r.contains(c));
        c.center.setZero();
        c.radius = 99;
        assertFalse(r.contains(c));
    }

    @Test
    public void setCenter() {
        Rectangle r = new Rectangle();

        r.setWithCenter(new Vec2(100, 200), 10, 20);
        assertTrue(r.minx == 100 - 10 / 2);
        assertTrue(r.maxx == 100 + 10 / 2);
        assertTrue(r.miny == 200 - 20 / 2);
        assertTrue(r.maxy == 200 + 20 / 2);

        r.setWithCenter(new Vec2(7, 9), 0, 0);
        assertTrue(r.containsClosed(new Vec2(7, 9)));
        assertFalse(r.containsOpen(new Vec2(7, 9)));
    }

    @Test
    public void scaleArea() {
        Rectangle r = new Rectangle(1, 1, 2, 2);
        assertTrue(r.area() == 1);
        assertTrue(r.center().equals(new Vec2(1.5f, 1.5f)));
        r.scale(2);
        assertTrue(r.center().equals(new Vec2(1.5f, 1.5f)));
        assertTrue(r.width() == 2);
        assertTrue(r.height() == 2);
        assertTrue(r.area() == 4);
    }

    @Test
    public void bound() {
        ArrayList<Vec2> points = new ArrayList<>();
        points.add(new Vec2(1, 1));
        assertTrue(Rectangle.bound(points).area() == 0);
        points.add(new Vec2(3, 1));
        assertTrue(Rectangle.bound(points).area() == 0);
        points.add(new Vec2(-2, 1));
        points.add(new Vec2(1, -2));
        Rectangle r = Rectangle.bound(points);
        assertTrue(r.minx == -2);
        assertTrue(r.maxx == 3);
        assertTrue(r.miny == -2);
        assertTrue(r.maxy == 1);
    }
}
