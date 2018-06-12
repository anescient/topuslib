package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import static junit.framework.Assert.*;


public class InternalAngleTest {

    private static SuperRandom sRandom = new SuperRandom(1234);

    @Test
    public void rightAngles() {
        LazyInternalAngle lia;

        lia = new LazyInternalAngle(new Vec3(0, 0, 1), new Vec3(0, 1, 0));
        assertTrue(epsilonEquals(lia.radians(), Math.PI / 2));
        assertTrue(lia.sine() == 1);
        assertTrue(lia.cosine() == 0);

        lia = new LazyInternalAngle(new Vec3(0, 0, 1), new Vec3(1, 0, 0));
        assertTrue(epsilonEquals(lia.radians(), Math.PI / 2));
        assertTrue(lia.sine() == 1);
        assertTrue(lia.cosine() == 0);

        lia = new LazyInternalAngle(new Vec3(1, 0, 0), new Vec3(0, 1, 0));
        assertTrue(epsilonEquals(lia.radians(), Math.PI / 2));
        assertTrue(lia.sine() == 1);
        assertTrue(lia.cosine() == 0);

        for(Vec3 v : Vec3Test.someRandomUnitVectors(100)) {
            Vec3 a = Vec3.arbitraryPerpendicular(v);
            lia = new LazyInternalAngle(v, a);
            assertTrue(epsilonEquals(lia.radians(), Math.PI / 2));
            assertTrue(epsilonEquals(lia.sine(), 1));
            assertTrue(epsilonEquals(lia.cosine(), 0));
        }
    }

    @Test
    public void coincident() {
        LazyInternalAngle lia;
        for(Vec3 v : Vec3Test.someRandomUnitVectors(100)) {
            lia = new LazyInternalAngle(v, v);
            assertTrue(lia.radians() == 0);
            assertTrue(lia.sine() == 0);
            assertTrue(lia.cosine() == 1);
        }
    }

    @Test
    public void opposed() {
        LazyInternalAngle lia;
        for(Vec3 v : Vec3Test.someRandomUnitVectors(100)) {
            lia = new LazyInternalAngle(v, v.negated());
            assertTrue(epsilonEquals(lia.radians(), Math.PI));
            assertTrue(epsilonEquals(lia.sine(), 0));
            assertTrue(epsilonEquals(lia.cosine(), -1));
        }
    }

    @Test
    public void randomCases() {
        for(int i = 0; i < 100; i++) {
            double a = sRandom.nextDouble() * Math.PI;
            Vec3 v = new Vec3((float)Math.cos(a), (float)Math.sin(a), 0);
            Vec3 unit = new Vec3(1, 0, 0);
            LazyInternalAngle lia = new LazyInternalAngle(v, unit);
            assertTrue(epsilonEquals(lia.radians(), a));
            assertTrue(epsilonEquals(lia.sine(), Math.sin(a)));
            assertTrue(epsilonEquals(lia.cosine(), Math.cos(a)));
        }
    }

    private static boolean epsilonEquals(double a, double b) {
        return Math.abs(a - b) < 0.00001;
    }
}
