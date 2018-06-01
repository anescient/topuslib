package net.chaosworship.topuslib.geom3d;

import org.junit.Test;

import static junit.framework.Assert.*;


public class OrthonormalBasisTest {

    @Test
    public void identityDefault() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        assertTrue(uvw.u.equals(new Vec3(1, 0, 0)));
        assertTrue(uvw.v.equals(new Vec3(0, 1, 0)));
        assertTrue(uvw.w.equals(new Vec3(0, 0, 1)));
        for(Vec3 p : Vec3Test.someRandomVectors(100)) {
            Vec3 q = new Vec3(p);
            uvw.transform(q);
            assertTrue(p.epsilonEquals(q, 0.000001f));
            assertTrue(q.epsilonEquals(uvw.transformed(p), 0.000001f));
        }
    }

    @Test
    public void orthogonal() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        assertOrthogonal(uvw.u, uvw.v);
        assertOrthogonal(uvw.v, uvw.w);
        assertOrthogonal(uvw.w, uvw.u);
        for(Vec3 p : Vec3Test.someRandomUnitVectors(100)) {
            uvw.setArbitraryAboutUnit(p);
            assertOrthogonal(uvw.u, uvw.v);
            assertOrthogonal(uvw.v, uvw.w);
            assertOrthogonal(uvw.w, uvw.u);
            assertTrue(uvw.isRightHanded());
        }
    }

    @Test
    public void rightHanded() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        assertTrue(uvw.isRightHanded());
        uvw.w.negate();
        assertFalse(uvw.isRightHanded());
        uvw.u.negate();
        assertTrue(uvw.isRightHanded());
        uvw.v.negate();
        assertFalse(uvw.isRightHanded());
    }

    @Test
    public void preservesMagnitude() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        for(Vec3 basis : Vec3Test.someRandomUnitVectors(100)) {
            uvw.setArbitraryAboutUnit(basis);
            for(Vec3 p : Vec3Test.someRandomVectors(100)) {
                Vec3 q = new Vec3(p);
                uvw.transform(q);
                float pmag = p.magnitude();
                float qmag = q.magnitude();
                assertTrue(Math.abs(pmag - qmag) < 0.00001);
            }
        }
    }

    private static void assertOrthogonal(Vec3 a, Vec3 b) {
        assertTrue(!a.isZero() && !b.isZero());
        assertTrue(Math.abs(a.dot(b)) < 0.00001f);
    }
}
