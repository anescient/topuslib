package net.chaosworship.topuslib.random;

import net.chaosworship.topuslib.CollectionTester;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertTrue;


public class SuperRandomTest {

    private static final SuperRandom sSuperRandom = new SuperRandom(1234);

    @Test
    public void approxGaussianSane() {
        assertApproxGaussianSane(0, 1, 17);
        assertApproxGaussianSane(0, 10, 3);
        assertApproxGaussianSane(0, 10, 11);
        assertApproxGaussianSane(99, 1, 17);
        assertApproxGaussianSane(-99, 2, 13);
    }

    private static void assertApproxGaussianSane(float mean, float stddev, int samples) {
        int n = 1000;
        ArrayList<Float> values = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            values.add(sSuperRandom.approximateGaussianFloat(mean, stddev, samples));
        }

        float maxdev = stddev * (float)(samples / Math.sqrt(samples / 3.0));
        float minBound = mean - maxdev * 1.1f;
        float maxBound = mean + maxdev * 1.1f;
        for(Float value : values) {
            assertTrue(value > minBound && value < maxBound);
        }

        float average = 0;
        for(Float value : values) {
            average += value;
        }
        average /= n;
        float averageBound = 0.5f * stddev;
        assertTrue(average - averageBound < mean && average + averageBound > mean);

        int balance = 0;
        for(Float value : values) {
            balance += value > mean ? 1 : -1;
        }
        int maxUnbalance = n / 4;
        assertTrue(balance > -maxUnbalance && balance < maxUnbalance);
    }

    @Test
    public void shuffleList() {
        ArrayList<Object> values = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            values.add(new Object());
        }

        // add some duplicates
        for(int i = 0; i < 10; i++) {
            values.add(values.get(i));
        }

        ArrayList<Object> shuffled = new ArrayList<>(values);
        sSuperRandom.shuffle(shuffled);
        assertTrue(CollectionTester.unorderedReferencesEqual(values, shuffled));
    }

    @Test
    public void shuffleArray() {
        Object[] values = new Object[60];
        for(int i = 0; i < 50; i++) {
            values[i] = new Object();
        }

        // add some duplicates
        for(int i = 50; i < 60; i++) {
            values[i] = values[i % 50];
        }

        Object[] shuffled = Arrays.copyOf(values, values.length);
        sSuperRandom.shuffle(shuffled);
        assertTrue(CollectionTester.unorderedReferencesEqual(values, shuffled));
    }

    @Test
    public void subShuffleArray() {
        int n = 50;
        Object[] values = new Object[n];
        for(int i = 0; i < n; i++) {
            values[i] = new Object();
        }
        Object a = values[0];
        Object b = values[1];
        Object x = values[n - 1];
        Object y = values[n - 2];
        Object z = values[n - 3];
        for(int i = 0; i < 20; i++) {
            sSuperRandom.subShuffle(values, 2, n - 5);
            assertTrue(a == values[0]);
            assertTrue(b == values[1]);
            assertTrue(x == values[n - 1]);
            assertTrue(y == values[n - 2]);
            assertTrue(z == values[n - 3]);
        }
    }
}
