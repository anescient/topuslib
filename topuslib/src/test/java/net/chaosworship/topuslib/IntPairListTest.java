package net.chaosworship.topuslib;

import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.collection.IntPairList;

import org.junit.Test;

import static junit.framework.Assert.*;


public class IntPairListTest {

    @Test
    public void empty() {
        IntPairList ipl = new IntPairList();
        assertTrue(ipl.getPairCount() == 0);
        IntPairConsumer consumer = new IntPairConsumer() {
            @Override
            public void putIntPair(int a, int b) {
                fail();
            }
        };
        ipl.putPairs(consumer);
    }

    @Test
    public void basicallyWorks() {
        IntPairList ipl = new IntPairList();
        ipl.add(1, 2);
        ipl.putIntPair(3, 4);
        assertTrue(ipl.getPairCount() == 2);
        IntPairConsumer counter = new IntPairConsumer() {
            int x = 1;
            @Override
            public void putIntPair(int a, int b) {
                assertTrue(a == x++);
                assertTrue(b == x++);
            }
        };
        ipl.putPairs(counter);
        ipl.clear();
        ipl.putIntPair(5, 6);
        ipl.putIntPair(7, 8);
        ipl.add(9, 10);
        ipl.putPairs(counter);
    }
}
