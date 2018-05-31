package net.chaosworship.topuslib.geom3d;

import net.chaosworship.topuslib.random.SuperRandom;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class Vec3Test {

    @Test
    public void arbitraryPerpendicular() {
        SuperRandom random = new SuperRandom(1234);
        Vec3 a = new Vec3();
        Vec3 b = new Vec3();
        a.setArbitraryPerpendicular(b);
        for(int i = 0; i < 100; i++) {
            a.set(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f);
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
}
