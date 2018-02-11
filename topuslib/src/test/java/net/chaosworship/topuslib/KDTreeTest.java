package net.chaosworship.topuslib;

import net.chaosworship.topuslib.math.Rectangle;
import net.chaosworship.topuslib.math.Vec2;
import net.chaosworship.topuslib.math.rangesearch.PointValuePair;
import net.chaosworship.topuslib.math.rangesearch.KDTree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;


public class KDTreeTest {

    @Test
    public void emptySet() throws Exception {
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        KDTree<String> kdtree = new KDTree<>();
        kdtree.load(pointValues);
        assertTrue(kdtree.search(new Rectangle(-1, -1, 1, 1)).isEmpty());
    }

    @Test
    public void singlePoint() throws Exception {
        Vec2 point = new Vec2(12, 34);
        String value = "abc";
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(new PointValuePair<>(point, value));
        KDTree<String> kdtree = new KDTree<>();
        kdtree.load(pointValues);
        List<String> results;

        results = kdtree.search(new Rectangle(-99, -99, 99, 99));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = kdtree.search(new Rectangle(12, 34, 12, 34));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = kdtree.search(new Rectangle(-1, -1, 1, 1));
        assertTrue(results.isEmpty());
    }

    @Test
    public void randomWithReload() throws Exception {
        Random random = new Random(1234);
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            Vec2 point = new Vec2(random.nextFloat(), random.nextFloat());
            pointValues.add(new PointValuePair<>(point, Integer.toString(i)));
        }

        KDTree<String> kdTree = new KDTree<>();
        kdTree.load(pointValues);

        HashSet<Integer> resultSizes = new HashSet<>();

        for(int i = 0; i < 100; i++) {
            Rectangle area = new Rectangle();
            area.setWithCenter(new Vec2(random.nextFloat(), random.nextFloat()), random.nextFloat(), random.nextFloat());
            ArrayList<String> expected = bruteSearch(area, pointValues);
            assertTrue(CollectionTester.unorderedReferencesEqual(kdTree.search(area), expected));
            resultSizes.add(expected.size());
        }
        assertTrue(resultSizes.size() == 41);

        for(PointValuePair<String> pvp : pointValues) {
            if(random.nextFloat() < 0.3) {
                pvp.point.set(random.nextFloat() * 2, random.nextFloat() * 2);
            }
        }
        kdTree.reload();
        resultSizes.clear();
        for(int i = 0; i < 100; i++) {
            Rectangle area = new Rectangle();
            area.setWithCenter(new Vec2(random.nextFloat(), random.nextFloat()), random.nextFloat(), random.nextFloat());
            ArrayList<String> expected = bruteSearch(area, pointValues);
            assertTrue(CollectionTester.unorderedReferencesEqual(kdTree.search(area), expected));
            resultSizes.add(expected.size());
        }
        assertTrue(resultSizes.size() == 31);
    }

    @Test
    public void duplicatePoints() throws Exception {
        PointValuePair<String> A = new PointValuePair<>(new Vec2(-1, -1), "a");
        PointValuePair<String> B = new PointValuePair<>(new Vec2(-1, -1), "b");

        PointValuePair<String> C = new PointValuePair<>(new Vec2(1, 1), "c");
        PointValuePair<String> D = new PointValuePair<>(new Vec2(1, 1), "d");
        PointValuePair<String> E = new PointValuePair<>(new Vec2(1, 1), "e");
        PointValuePair<String> F = new PointValuePair<>(new Vec2(1, 1), "f");
        PointValuePair<String> G = new PointValuePair<>(new Vec2(1, 1), "g");

        PointValuePair<String> H = new PointValuePair<>(new Vec2(1, 2), "h");
        PointValuePair<String> I = new PointValuePair<>(new Vec2(2, 1), "i");

        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(A);
        pointValues.add(B);
        pointValues.add(C);
        pointValues.add(D);
        pointValues.add(E);
        pointValues.add(F);
        pointValues.add(G);
        pointValues.add(H);
        pointValues.add(I);

        KDTree<String> kdTree = new KDTree<>();
        kdTree.load(pointValues);

        ArrayList<String> expected = new ArrayList<>();
        for(PointValuePair<String> pvp : pointValues) {
            expected.add(pvp.value);
        }

        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-10, -10, 10, 10)),
                expected));

        expected.clear();
        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1.5f, -1.5f, -0.5f, -0.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1, -1, -1, -1)),
                expected));

        expected.clear();
        expected.add(C.value);
        expected.add(D.value);
        expected.add(E.value);
        expected.add(F.value);
        expected.add(G.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(0.5f, 0.5f, 1.5f, 1.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(1, 1, 1, 1)),
                expected));

        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1.5f, -1.5f, 1.5f, 1.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1, -1, 1, 1)),
                expected));
    }

    @Test
    public void closedBound() throws Exception {
        PointValuePair<String> A = new PointValuePair<>(new Vec2(-1, 0), "a");
        PointValuePair<String> B = new PointValuePair<>(new Vec2(1, 0), "b");
        PointValuePair<String> C = new PointValuePair<>(new Vec2(0, -1), "c");
        PointValuePair<String> D = new PointValuePair<>(new Vec2(0, 1), "d");

        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(A);
        pointValues.add(B);
        pointValues.add(C);
        pointValues.add(D);

        KDTree<String> kdTree = new KDTree<>();
        kdTree.load(pointValues);

        assertTrue(kdTree.search(new Rectangle(-0.5f, -0.5f, 0.5f, 0.5f)).isEmpty());

        ArrayList<String> expected = new ArrayList<>();
        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1, 0, 1, 0)),
                expected));

        expected.clear();
        expected.add(C.value);
        expected.add(D.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(0, -1, 0, 1)),
                expected));

        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                kdTree.search(new Rectangle(-1, -1, 1, 1)),
                expected));
    }

    private static<TT> ArrayList<TT> bruteSearch(Rectangle area, Iterable<PointValuePair<TT>> pointValues) {
        ArrayList<TT> results = new ArrayList<>();
        for(PointValuePair<TT> pvp : pointValues) {
            if(area.containsClosed(pvp.point)) {
                results.add(pvp.value);
            }
        }
        return results;
    }
}
