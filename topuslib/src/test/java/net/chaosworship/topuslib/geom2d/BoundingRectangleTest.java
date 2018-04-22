package net.chaosworship.topuslib.geom2d;

import org.junit.Test;

import static junit.framework.Assert.*;


public class BoundingRectangleTest {

    @Test
    public void rectangle() {
        Rectangle r = new Rectangle(1, 3, 5, 7);
        assertTrue(r.area() > 0);
        Rectangle s = r.getBoundingRectangle();
        assertFalse(s == r);
        assertTrue(s.equals(r));
    }

    @Test
    public void circle() {
        Circle c = new Circle(new Vec2(4, 5), 7);
        Rectangle expected = new Rectangle(-3, -2, 11, 12);
        assertTrue(c.getBoundingRectangle().equals(expected));
    }
}
