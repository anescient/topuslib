package net.chaosworship.topuslib;

import net.chaosworship.topuslib.math.Angles;

import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.*;


public class AngleTest {

    @Test
    public void unloop() throws Exception {
        Random random = new Random(1234);

        for(int i = 0; i < 100; i++) {
            double a = random.nextDouble() * 2 * Math.PI;
            assertTrue(probablyEqual(a, Angles.unloopRadians(a)));
        }

        assertTrue(Angles.unloopRadians(0) == 0);
        assertTrue(Angles.unloopRadians(2 * Math.PI) == 0);

        for(int i = 0; i < 100; i++) {
            double a = (random.nextDouble() - 0.5) * 20;
            assertTrue(probablyEqual(Math.cos(a), Math.cos(Angles.unloopRadians(a))));
        }
    }

    private static boolean probablyEqual(double a, double b) {
        return Math.abs(a - b) < 0.00000001;
    }
}
