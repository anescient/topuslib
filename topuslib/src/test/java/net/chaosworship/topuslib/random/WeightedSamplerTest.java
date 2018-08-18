package net.chaosworship.topuslib.random;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;


public class WeightedSamplerTest {

    private static final SuperRandom sSuperRandom = new SuperRandom(1234);

    @Test
    public void equalWeights() {
        final int n = 11;
        WeightedSampler<Integer> sampler = new WeightedSampler<>();
        ArrayList<Integer> counts = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            sampler.insert(1f, i);
            counts.add(i);
        }
        for(int i = 0; i < 10000; i++) {
            int x = sampler.sample(sSuperRandom);
            counts.set(x, counts.get(x) + 1);
        }
        for(int i = 0; i < n; i++) {
            assertTrue(counts.get(i) > 800 && counts.get(i) < 1000);
        }
    }

    @Test
    public void differentWeights() {
        WeightedSampler<Integer> sampler = new WeightedSampler<>();
        ArrayList<Integer> counts = new ArrayList<>();
        sampler.insert(10, 0);
        sampler.insert(20, 1);
        sampler.insert(50, 2);
        counts.add(0);
        counts.add(0);
        counts.add(0);
        for(int i = 0; i < 10000; i++) {
            int x = sampler.sample(sSuperRandom);
            counts.set(x, counts.get(x) + 1);
        }
        assertTrue(counts.get(0) > 1100 && counts.get(0) < 1300);
        assertTrue(counts.get(1) > 2400 && counts.get(1) < 2600);
        assertTrue(counts.get(2) > 5000 && counts.get(2) < 7000);
    }
}
