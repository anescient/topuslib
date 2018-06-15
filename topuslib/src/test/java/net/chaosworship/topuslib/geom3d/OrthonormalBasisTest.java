package net.chaosworship.topuslib.geom3d;

import org.junit.Test;

import java.util.ArrayList;

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
            uvw.transformFromStandardBasis(q);
            assertTrue(p.epsilonEquals(q, 0.000001f));
            assertTrue(q.epsilonEquals(uvw.transformedFromStandardBasis(p), 0.000001f));
        }
    }

    @Test
    public void orthogonal() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        assertOrthogonal(uvw);
        for(Vec3 p : Vec3Test.someRandomVectors(100)) {
            uvw.setArbitraryAboutW(p);
            assertOrthogonal(uvw);
            assertTrue(uvw.isRightHanded());
            p.negate();
            uvw.setArbitraryAboutW(p);
            assertOrthogonal(uvw);
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
        uvw.v.negate();
        assertTrue(uvw.isRightHanded());
    }

    @Test
    public void setRightHandedTest() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        for(Vec3 p : Vec3Test.someRandomUnitVectors(100)) {
            Vec3 q = new Vec3(1, 2, 3);
            uvw.setRightHanded(p, q);
            assertTrue(uvw.isRightHanded());
            assertOrthogonal(uvw.u, uvw.v);
            assertOrthogonal(uvw.v, uvw.w);
            assertOrthogonal(uvw.w, uvw.u);
        }
    }

    @Test
    public void preservesMagnitude() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        for(Vec3 basis : Vec3Test.someRandomUnitVectors(100)) {
            uvw.setArbitraryAboutW(basis);
            for(Vec3 p : Vec3Test.someRandomVectors(100)) {
                Vec3 q = new Vec3(p);
                uvw.transformFromStandardBasis(q);
                float pmag = p.magnitude();
                float qmag = q.magnitude();
                assertTrue(Math.abs(pmag - qmag) < 0.00001);
            }
        }
    }

    @Test
    public void reallign() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        assertTrue(uvw.isRightHanded());
        for(Vec3 w : Vec3Test.someRandomVectors(100)) {
            uvw.rotateToW(w);
            assertOrthogonal(uvw);
            assertTrue(uvw.isRightHanded());
        }
    }

    @Test
    public void reallignRightAngles() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        ArrayList<Vec3> alignments = new ArrayList<>();
        alignments.add(new Vec3(1, 0, 0));
        alignments.add(new Vec3(0, 1, 0));
        alignments.add(new Vec3(0, 0, 1));
        alignments.add(new Vec3(1, 0, 0));
        alignments.add(new Vec3(0, 0, 1));
        alignments.add(new Vec3(-1, 0, 0));
        alignments.add(new Vec3(0, -1, 0));
        alignments.add(new Vec3(0, 1, 0));
        alignments.add(new Vec3(0, 0, -1));
        for(Vec3 alignment : alignments) {
            uvw.rotateToW(alignment);
            assertOrthogonal(uvw);
            assertTrue(uvw.isRightHanded());
        }
    }

    @Test
    public void roundTrip() {
        OrthonormalBasis uvw = new OrthonormalBasis();
        for(Vec3 w : Vec3Test.someRandomVectors(10)) {
            uvw.setArbitraryAboutW(w);
            for(Vec3 p : Vec3Test.someRandomVectors(100)) {
                Vec3 q = uvw.transformedFromStandardBasis(p);
                assertEqual(p, uvw.transformedToStandardBasis(q));
            }
        }
    }

    private static void assertOrthogonal(OrthonormalBasis basis) {
        assertOrthogonal(basis.u, basis.v);
        assertOrthogonal(basis.v, basis.w);
        assertOrthogonal(basis.w, basis.u);
    }

    private static void assertOrthogonal(Vec3 a, Vec3 b) {
        assertTrue(!a.isZero() && !b.isZero());
        assertTrue(Math.abs(a.dot(b)) < 0.00001f);
    }

    private static void assertEqual(Vec3 a, Vec3 b) {
        assertTrue(Math.abs(a.x - b.x) < 0.00001f);
        assertTrue(Math.abs(a.y - b.y) < 0.00001f);
        assertTrue(Math.abs(a.z - b.z) < 0.00001f);
    }
}
