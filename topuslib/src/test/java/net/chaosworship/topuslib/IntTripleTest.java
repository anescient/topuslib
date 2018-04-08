package net.chaosworship.topuslib;

import net.chaosworship.topuslib.tuple.IntPair;
import net.chaosworship.topuslib.tuple.IntTriple;

import org.junit.Test;

import static junit.framework.Assert.*;


public class IntTripleTest {

    @Test
    public void equalsAndHash() {
        IntTriple t1 = new IntTriple(1, 2, 3);
        IntTriple t2 = new IntTriple(1, 2, 3);
        IntTriple t3 = new IntTriple(1, 3, 2);
        assertTrue(t1.equals(t2));
        assertTrue(t1.hashCode() == t2.hashCode());
        assertFalse(t1.equals(t3));
        assertFalse(t2.equals(t3));
    }

    @Test
    public void sortedIsSorted() {
        assertFalse(isSorted(new IntTriple(3, 2, 1)));
        assertTrue(isSorted(IntTriple.sorted(1, 2, 3)));
        assertTrue(isSorted(IntTriple.sorted(1, 3, 2)));
        assertTrue(isSorted(IntTriple.sorted(2, 1, 3)));
        assertTrue(isSorted(IntTriple.sorted(2, 3, 1)));
        assertTrue(isSorted(IntTriple.sorted(3, 2, 1)));
        assertTrue(isSorted(IntTriple.sorted(3, 1, 2)));
    }

    @Test
    public void includesPair() {
        IntTriple t = new IntTriple(4, 5, 6);
        assertFalse(t.includesPair(new IntPair(4, 4)));
        assertTrue(t.includesPair(new IntPair(4, 5)));
        assertTrue(t.includesPair(new IntPair(5, 4)));
        assertTrue(t.includesPair(new IntPair(4, 6)));
        assertTrue(t.includesPair(new IntPair(6, 4)));
        assertTrue(t.includesPair(new IntPair(5, 6)));
        assertTrue(t.includesPair(new IntPair(6, 5)));
        t = new IntTriple(4, 4, 5);
        assertTrue(t.includesPair(new IntPair(4, 4)));
        assertFalse(t.includesPair(new IntPair(5, 5)));
    }

    @Test
    public void getThird() {
        IntTriple t = new IntTriple(1, 2, 3);
        assertTrue(t.getThird(new IntPair(1, 2)) == 3);
        assertTrue(t.getThird(new IntPair(2, 1)) == 3);
        assertTrue(t.getThird(new IntPair(2, 3)) == 1);
        assertTrue(t.getThird(new IntPair(3, 2)) == 1);
        assertTrue(t.getThird(new IntPair(1, 3)) == 2);
        assertTrue(t.getThird(new IntPair(3, 1)) == 2);
        t = new IntTriple(4, 4, 5);
        assertTrue(t.getThird(new IntPair(4, 4)) == 5);
        assertTrue(t.getThird(new IntPair(4, 5)) == 4);
        assertTrue(t.getThird(new IntPair(5, 4)) == 4);
    }

    private static boolean isSorted(IntTriple intTriple) {
        return intTriple.a <= intTriple.b && intTriple.b <= intTriple.c;
    }
}
