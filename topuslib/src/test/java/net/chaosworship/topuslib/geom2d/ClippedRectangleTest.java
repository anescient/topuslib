package net.chaosworship.topuslib.geom2d;

import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import static org.junit.Assert.*;


public class ClippedRectangleTest {

    private static final SuperRandom sRandom = new SuperRandom();

    @Test
    public void infinite() {
        ClippedRectangle cr = new ClippedRectangle();
        for(int i = 0; i < 1000; i++) {
            float x = sRandom.uniformInRange(-1000, 1000);
            float y = sRandom.uniformInRange(-2000, 999);
            assertTrue(cr.containsOpen(x, y));
        }

        assertTrue(cr.containsOpen(0, 0));
        assertTrue(cr.containsOpen(999999999f, 999999999f));
        assertTrue(cr.containsOpen(-999999999f, 999999999f));
        assertTrue(cr.containsOpen(-999999999f, -999999999f));
        assertTrue(cr.containsOpen(999999999f, -999999999f));
    }

    @Test
    public void halfPlane() {
        ClippedRectangle cr = new ClippedRectangle();

        assertTrue(cr.containsOpen(11, 0));
        cr.clipMaxX(10);
        assertFalse(cr.containsOpen(11, 0));
        assertTrue(cr.containsOpen(-1, 0));
        cr.clipMaxX(0);
        assertTrue(cr.containsOpen(-1, 999));
        assertTrue(cr.containsOpen(-999999999, 999));

        cr.clipMinX(10);
        cr.clipMaxX(-10);
        assertFalse(cr.containsOpen(0, 0));

        cr.clipMinX(-10);
        cr.clipMaxX(10);
        assertFalse(cr.containsOpen(0, 0));
        cr.setUnbounded();
        assertTrue(cr.containsOpen(0, 0));

        assertTrue(cr.containsOpen(11, 0));
        cr.clipMaxX(12);
        assertTrue(cr.containsOpen(11, 0));
        cr.clipMinX(13);
        for(float x = -10; x < 10; x += 0.33f) {
            assertFalse(cr.containsOpen(x, 123));
        }

        cr.setUnbounded();
        assertTrue(cr.containsOpen(0, 2));
        assertTrue(cr.containsOpen(9999999, -9999999));
        cr.clipMinY(3);
        assertFalse(cr.containsOpen(0, 2));
        assertTrue(cr.containsOpen(9999999, 9999999));
        cr.clipMinX(4);
        assertTrue(cr.containsOpen(9999999, 9999999));
        cr.clipMaxY(99);
        assertFalse(cr.containsOpen(9999999, 9999999));
    }

    @Test
    public void copy() {
        ClippedRectangle cr = new ClippedRectangle();
        cr.clipMaxY(10);
        cr.clipMinX(-10);
        assertTrue(cr.containsOpen(-4, 4));
        assertFalse(cr.containsOpen(-9999, 9));
        ClippedRectangle cr2 = new ClippedRectangle(cr);
        assertTrue(cr2.containsOpen(-4, 4));
        assertFalse(cr2.containsOpen(-9999, 9));
    }

    @Test
    public void containsRect() {
        ClippedRectangle cr = new ClippedRectangle();
        Rectangle r = new Rectangle(-1, -1, 1, 1);
        assertTrue(cr.containsOpen(r));
        cr.clipMinX(-0.9f);
        assertFalse(cr.containsOpen(r));
        cr.setUnbounded();
        cr.clipMaxX(2);
        cr.clipMaxY(2);
        cr.clipMinX(-2);
        cr.clipMinY(-2);
        assertTrue(cr.containsOpen(r));
        r.minx = -2.1f;
        assertFalse(cr.containsOpen(r));
    }

    @Test
    public void overlapsRect() {
        ClippedRectangle cr = new ClippedRectangle();
        Rectangle r = new Rectangle(-99, -99, 99, 99);
        assertTrue(cr.overlapsRect(r));
        cr.clipMinX(-99);
        cr.clipMaxY(99);
        cr.clipMinY(-99);
        cr.clipMaxY(99);
        assertTrue(cr.overlapsRect(r));
        cr.clipMinX(-1);
        cr.clipMaxY(1);
        cr.clipMinY(-1);
        cr.clipMaxY(1);
        assertTrue(cr.overlapsRect(r));
        r = new Rectangle(1, 1, 2, 2);
        assertFalse(cr.overlapsRect(r));
        r.miny -= 0.000001f;
        r.minx -= 0.000001f;
        assertTrue(cr.overlapsRect(r));
        r = new Rectangle(0, 0, 0, 0);
        assertTrue(cr.overlapsRect(r));
        cr.setUnbounded();
        assertTrue(cr.overlapsRect(r));
        r = new Rectangle(1, 998, 2, 999);
        assertTrue(cr.overlapsRect(r));
        cr.clipMinY(1000);
        assertFalse(cr.overlapsRect(r));
    }

    @Test
    public void isContained() {
        ClippedRectangle cr = new ClippedRectangle();
        Rectangle r = new Rectangle(-100, -100, 100, 100);
        assertFalse(cr.isContainedBy(r));
        cr.clipMinX(-100);
        cr.clipMinY(-100);
        cr.clipMaxX(100);
        assertFalse(cr.isContainedBy(r));
        cr.clipMaxY(100);
        assertTrue(cr.isContainedBy(r));
        cr.setUnbounded();
        assertFalse(cr.isContainedBy(r));
        cr.clipMinY(-101);
        cr.clipMinX(-101);
        cr.clipMaxX(101);
        cr.clipMaxY(101);
        assertFalse(cr.isContainedBy(r));
        cr.clipMinY(0);
        cr.clipMaxY(0);
        assertFalse(cr.isContainedBy(r));
        r.minx = 0;
        r.maxx = 0;
        assertFalse(cr.isContainedBy(r));
        cr.clipMaxX(0);
        cr.clipMinX(0);
        assertTrue(cr.isContainedBy(r));
        r.miny = 0;
        r.maxy = 0;
        assertTrue(cr.isContainedBy(r));
    }

    @Test
    public void unflipable() {
        ClippedRectangle cr = new ClippedRectangle();
        cr.clipMinX(1);
        cr.clipMaxX(-1);
        cr.clipMinY(1);
        cr.clipMaxY(-1);
        Rectangle r = cr.asRectangle();
        assertTrue(r.height() == 0);
        assertTrue(r.width() == 0);
    }
}
