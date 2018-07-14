package net.chaosworship.topuslib.geom2d;

import net.chaosworship.topuslib.CollectionTester;
import net.chaosworship.topuslib.geom2d.rangesearch.KDTree;
import net.chaosworship.topuslib.tuple.PointValuePair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;


public class KDTreeTest {

    private static final KDTree<String> mStringTree = new KDTree<>();

    @Test
    public void emptySet() {
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        mStringTree.load(pointValues);
        assertTrue(mStringTree.search(new Rectangle(-1, -1, 1, 1)).isEmpty());
    }

    @Test
    public void singlePoint() {
        Vec2 point = new Vec2(12, 34);
        String value = "abc";
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(new PointValuePair<>(point, value));
        mStringTree.load(pointValues);
        List<String> results;

        results = mStringTree.search(new Rectangle(-99, -99, 99, 99));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = mStringTree.search(new Rectangle(12, 34, 12, 34));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = mStringTree.search(new Rectangle(-1, -1, 1, 1));
        assertTrue(results.isEmpty());
    }

    @Test
    public void randomWithReload() {
        Random random = new Random(1234);
        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            Vec2 point = new Vec2(random.nextFloat(), random.nextFloat());
            pointValues.add(new PointValuePair<>(point, Integer.toString(i)));
        }

        mStringTree.load(pointValues);

        HashSet<Integer> resultSizes = new HashSet<>();

        for(int i = 0; i < 100; i++) {
            Rectangle area = new Rectangle();
            area.setWithCenter(new Vec2(random.nextFloat(), random.nextFloat()), random.nextFloat(), random.nextFloat());
            List<String> expected = bruteSearch(area, pointValues);
            List<String> calculated = mStringTree.search(area);
            assertTrue(CollectionTester.unorderedReferencesEqual(calculated, expected));
            resultSizes.add(expected.size());
        }
        assertTrue(resultSizes.size() == 41);

        for(PointValuePair<String> pvp : pointValues) {
            if(random.nextFloat() < 0.3) {
                pvp.point.set(random.nextFloat() * 2, random.nextFloat() * 2);
            }
        }

        mStringTree.reload();
        resultSizes.clear();
        for(int i = 0; i < 100; i++) {
            Rectangle area = new Rectangle();
            area.setWithCenter(new Vec2(random.nextFloat(), random.nextFloat()), random.nextFloat(), random.nextFloat());
            ArrayList<String> expected = bruteSearch(area, pointValues);
            assertTrue(CollectionTester.unorderedReferencesEqual(mStringTree.search(area), expected));
            resultSizes.add(expected.size());
        }
        assertTrue(resultSizes.size() == 31);
    }

    @Test
    public void edgeTrap() {
        PointValuePair<String> A = new PointValuePair<>(new Vec2(0, 1), "a");
        PointValuePair<String> B = new PointValuePair<>(new Vec2(0, 0), "b");
        PointValuePair<String> C = new PointValuePair<>(new Vec2(0, -1), "c");
        PointValuePair<String> D = new PointValuePair<>(new Vec2(1, 0), "d");
        PointValuePair<String> E = new PointValuePair<>(new Vec2(-1, 0), "e");

        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(A);
        pointValues.add(B);
        pointValues.add(C);
        pointValues.add(D);
        pointValues.add(E);

        mStringTree.load(pointValues);
        ArrayList<String> expected = new ArrayList<>();

        expected.clear();
        expected.add(A.value);
        expected.add(B.value);
        expected.add(C.value);
        expected.add(E.value);
        ArrayList<String> left = new ArrayList<>(mStringTree.search(new Rectangle(-1, -1, 0, 1)));
        assertTrue(CollectionTester.unorderedReferencesEqual(expected, left));

        expected.remove(E.value);
        expected.add(D.value);
        ArrayList<String> right = new ArrayList<>(mStringTree.search(new Rectangle(0, -1, 1, 1)));
        assertTrue(CollectionTester.unorderedReferencesEqual(expected, right));
    }

    @Test
    public void duplicatePoints() {
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

        mStringTree.load(pointValues);

        ArrayList<String> expected = new ArrayList<>();
        for(PointValuePair<String> pvp : pointValues) {
            expected.add(pvp.value);
        }

        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-10, -10, 10, 10)),
                expected));

        expected.clear();
        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1.5f, -1.5f, -0.5f, -0.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1, -1, -1, -1)),
                expected));

        expected.clear();
        expected.add(C.value);
        expected.add(D.value);
        expected.add(E.value);
        expected.add(F.value);
        expected.add(G.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(0.5f, 0.5f, 1.5f, 1.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(1, 1, 1, 1)),
                expected));

        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1.5f, -1.5f, 1.5f, 1.5f)),
                expected));
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1, -1, 1, 1)),
                expected));
    }

    @Test
    public void closedBound() {
        PointValuePair<String> A = new PointValuePair<>(new Vec2(-1, 0), "a");
        PointValuePair<String> B = new PointValuePair<>(new Vec2(1, 0), "b");
        PointValuePair<String> C = new PointValuePair<>(new Vec2(0, -1), "c");
        PointValuePair<String> D = new PointValuePair<>(new Vec2(0, 1), "d");

        ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
        pointValues.add(A);
        pointValues.add(B);
        pointValues.add(C);
        pointValues.add(D);

        mStringTree.load(pointValues);

        assertTrue(mStringTree.search(new Rectangle(-0.5f, -0.5f, 0.5f, 0.5f)).isEmpty());

        ArrayList<String> expected = new ArrayList<>();
        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1, 0, 1, 0)),
                expected));

        expected.clear();
        expected.add(C.value);
        expected.add(D.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(0, -1, 0, 1)),
                expected));

        expected.add(A.value);
        expected.add(B.value);
        assertTrue(CollectionTester.unorderedReferencesEqual(
                mStringTree.search(new Rectangle(-1, -1, 1, 1)),
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
