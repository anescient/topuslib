package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class Vec3Test {

    private static SuperRandom sRandom = new SuperRandom(1234);

    @Test
    public void arbitraryPerpendicular() {
        Vec3 a = new Vec3();
        Vec3 b = new Vec3();
        a.setArbitraryPerpendicular(b); // check that it doesn't croak
        for(Vec3 v : someRandomVectors(100)) {
            a.set(v);
            b.setArbitraryPerpendicular(a);
            assertTrue(Math.abs(a.dot(b)) < 0.00001);
        }

        ArrayList<Vec3> units = new ArrayList<>();
        units.add(new Vec3(1, 0, 0));
        units.add(new Vec3(0, 1, 0));
        units.add(new Vec3(0, 0, 1));
        units.add(new Vec3(-1, 0, 0));
        units.add(new Vec3(0, -1, 0));
        units.add(new Vec3(0, 0, -1));

        for(Vec3 unit : units) {
            a.set(unit);
            b.setArbitraryPerpendicular(a);
            assertTrue(Math.abs(a.dot(b)) < 0.000001);
        }
    }

    @Test
    public void randomUnitVectorsAreUnit() {
        for(Vec3 v : someRandomUnitVectors(100)) {
            assertTrue(epsilonMagnitudeEquals(v, 1));
        }
    }

    static boolean epsilonMagnitudeEquals(Vec3 v, float mag) {
        return Math.abs(v.magnitude() - mag) < 0.000001;
    }

    static ArrayList<Vec3> someRandomUnitVectors(int count) {
        ArrayList<Vec3> vectors = new ArrayList<>();
        while(vectors.size() < count) {
            Vec3 v = new Vec3(sRandom.nextFloat() - 0.5f, sRandom.nextFloat() - 0.5f, sRandom.nextFloat() - 0.5f);
            if(!v.isZero()) {
                v.normalize();
                vectors.add(v);
            }
        }
        return vectors;
    }

    static ArrayList<Vec3> someRandomVectors(int count) {
        ArrayList<Vec3> vectors = someRandomUnitVectors(count);
        for(Vec3 v : vectors) {
            v.scale(0.1f + 4 * sRandom.nextFloat());
        }
        return vectors;
    }
}
