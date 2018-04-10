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
            public void addIntPair(int a, int b) {
                fail();
            }
        };
        ipl.outputPairs(consumer);
    }

    @Test
    public void basicallyWorks() {
        IntPairList ipl = new IntPairList();
        ipl.add(1, 2);
        ipl.addIntPair(3, 4);
        assertTrue(ipl.getPairCount() == 2);
        IntPairConsumer counter = new IntPairConsumer() {
            int x = 1;
            @Override
            public void addIntPair(int a, int b) {
                assertTrue(a == x++);
                assertTrue(b == x++);
            }
        };
        ipl.outputPairs(counter);
        ipl.clear();
        ipl.addIntPair(5, 6);
        ipl.addIntPair(7, 8);
        ipl.add(9, 10);
        ipl.outputPairs(counter);
    }
}
