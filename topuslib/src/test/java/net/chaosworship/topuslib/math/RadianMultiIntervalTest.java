package net.chaosworship.topuslib.math;

import org.junit.Test;

import java.util.Random;

import static junit.framework.Assert.*;


public class RadianMultiIntervalTest {

    @Test
    public void allOrNone() {
        RadianMultiInterval rmi = new RadianMultiInterval();
        assertFalse(rmi.isEmpty());
        assertTrue(rmi.getTotalRadians() == 2 * Math.PI);
        rmi.clear();
        assertTrue(rmi.isEmpty());
        assertTrue(rmi.getTotalRadians() == 0);
        rmi.setFullCircle();
        assertFalse(rmi.isEmpty());
        assertTrue(rmi.getTotalRadians() == 2 * Math.PI);
        rmi.exclude(1, 10);
        assertTrue(rmi.isEmpty());
    }

    @Test
    public void excludeIncludes() {
        RadianMultiInterval rmi = new RadianMultiInterval();
        assertTrue(rmi.includes(1.0));
        rmi.exclude(0.99, 1.01);
        assertFalse(rmi.includes(1.0));
        assertTrue(rmi.includes(0.98));
        assertTrue(rmi.includes(1.02));

        assertTrue(rmi.includes(0));
        rmi.exclude(6.2 - 2 * Math.PI, 0.1);
        assertFalse(rmi.includes(0));
        assertFalse(rmi.includes(0.05));
        assertFalse(rmi.includes(6.21));
        for(double a = 1.1; a < 6.1; a += 0.01) {
            assertTrue(rmi.includes(a));
        }
        rmi.exclude(4, 5);
        for(double a = 4.001; a < 4.999; a += 0.01) {
            assertFalse(rmi.includes(a));
        }
        rmi.exclude(1.9, 2.1);
        assertFalse(rmi.includes(2));
        assertTrue(rmi.includes(1.8));
        assertTrue(rmi.includes(2.2));
        for(double a = 2.2; a < 3.9; a += 0.01) {
            assertTrue(rmi.includes(a));
        }
    }

    @Test
    public void randomSample() {
        Random random = new Random(1234);
        RadianMultiInterval rmi = new RadianMultiInterval();
        double amin = -1;
        double amax = 1;
        rmi.exclude(amin, amax);
        double bmin = 1.1;
        double bmax = 2;
        rmi.exclude(bmin, bmax);
        double cmin = 3;
        double cmax = 3.5;
        rmi.exclude(cmin, cmax);
        double abcount = 0;
        double bccount = 0;
        double cacount = 0;
        double radsleft = rmi.getTotalRadians();
        int n = 10000;
        for(int i = 0; i < n; i++) {
            double s = rmi.uniformSample(random);
            if(s >= amax && s <= bmin) {
                abcount++;
            } else if(s >= bmax && s <= cmin) {
                bccount++;
            } else if(s >= cmax && s <= amin + 2 * Math.PI) {
                cacount++;
            } else {
                fail();
            }
        }
        assertTrue(roughlyEqual(abcount / n, (bmin - amax) / radsleft, 0.05));
        assertTrue(roughlyEqual(bccount / n, (cmin - bmax) / radsleft, 0.05));
        // if those two are correct, cacount will be, too
    }

    private static boolean roughlyEqual(double a, double b, double margin) {
        return Math.abs(a - b) < margin;
    }
}
