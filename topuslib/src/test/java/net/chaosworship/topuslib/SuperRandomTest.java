package net.chaosworship.topuslib;

import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;


public class SuperRandomTest {

    private static final SuperRandom sSuperRandom = new SuperRandom();

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
}
