package net.chaosworship.topuslib;


import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import org.junit.Test;

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
}
