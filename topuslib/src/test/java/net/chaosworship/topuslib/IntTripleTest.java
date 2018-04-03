package net.chaosworship.topuslib;

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

    private static boolean isSorted(IntTriple intTriple) {
        return intTriple.a <= intTriple.b && intTriple.b <= intTriple.c;
    }
}
