package net.chaosworship.topuslib.collection;

import net.chaosworship.topuslib.CollectionTester;
import net.chaosworship.topuslib.random.RandomQueue;
import net.chaosworship.topuslib.random.XORShiftRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class RandomQueueTest {

    @Test
    public void empty() {
        RandomQueue<Object> q = new RandomQueue<>();
        assertTrue(q.size() == 0);
        assertTrue(q.isEmpty());
        q.add(new Object());
        assertFalse(q.isEmpty());
    }

    @Test
    public void doesRandomize() {
        XORShiftRandom random = new XORShiftRandom(1234);
        RandomQueue<Object> q = new RandomQueue<>(random);
        ArrayList<Object> values = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            q.add(i);
            values.add(i);
        }
        ArrayList<Object> outValues = new ArrayList<>();
        while(!q.isEmpty()) {
            outValues.add(q.popRandom());
        }
        assertTrue(CollectionTester.unorderedReferencesEqual(values, outValues));
        assertFalse(CollectionTester.orderedReferencesEqual(values, outValues));
    }
}
