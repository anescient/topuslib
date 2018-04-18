package net.chaosworship.topuslib.tuple;

import org.junit.Test;

import static junit.framework.Assert.*;


public class IntPairTest {

    @Test
    public void equalsAndHash() {
        IntPair p1 = new IntPair(1, 2);
        IntPair p2 = new IntPair(1, 2);
        IntPair p3 = new IntPair(2, 1);
        assertTrue(p1.equals(p2));
        assertTrue(p1.hashCode() == p2.hashCode());
        assertFalse(p1.equals(p3));
    }

    @Test
    public void sortedIsSorted() {
        assertFalse(isSorted(new IntPair(2, 1)));
        assertTrue(isSorted(IntPair.sorted(1, 2)));
        assertTrue(isSorted(IntPair.sorted(2, 1)));
    }

    private static boolean isSorted(IntPair intPair) {
        return intPair.a <= intPair.b;
    }
}
