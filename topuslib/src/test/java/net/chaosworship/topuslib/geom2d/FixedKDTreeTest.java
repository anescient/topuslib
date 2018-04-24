package net.chaosworship.topuslib.geom2d;

import net.chaosworship.topuslib.CollectionTester;
import net.chaosworship.topuslib.geom2d.rangesearch.FixedKDTree;
import net.chaosworship.topuslib.tuple.PointValuePair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


public class FixedKDTreeTest {

    @Test
    public void emptySet() {
        Rectangle r = new Rectangle(-1, -1, 1, 1);
        assertTrue(new FixedKDTree(r).search(r).isEmpty());
    }

    @Test
    public void singlePoint() {
        Vec2 point = new Vec2(12, 34);
        String value = "abc";
        FixedKDTree<String> tree = new FixedKDTree<>(new Rectangle(-99, -99, 99, 99));
        tree.insert(point, value);
        List<String> results;

        results = tree.search(new Rectangle(-99, -99, 99, 99));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = tree.search(new Rectangle(12, 34, 12, 34));
        assertTrue(results.size() == 1);
        assertSame(results.get(0), value);

        results = tree.search(new Rectangle(-1, -1, 1, 1));
        assertTrue(results.isEmpty());
    }

    @Test
    public void random() {
        Random random = new Random(1234);
        for(int trial = 0; trial < 20; trial++) {
            ArrayList<PointValuePair<String>> pointValues = new ArrayList<>();
            for(int i = 0; i < (1 + trial) * 10; i++) {
                Vec2 point = new Vec2(random.nextFloat(), random.nextFloat());
                pointValues.add(new PointValuePair<>(point, Integer.toString(i)));
            }

            FixedKDTree<String> tree = new FixedKDTree<>(new Rectangle(-2, -2, 2, 2));
            for(PointValuePair<String> pvp : pointValues) {
                tree.insert(pvp);
            }

            for(int i = 0; i < 50; i++) {
                Rectangle area = new Rectangle();
                area.setWithCenter(new Vec2(random.nextFloat(), random.nextFloat()), random.nextFloat(), random.nextFloat());
                ArrayList<String> expected = bruteSearch(area, pointValues);
                assertTrue(CollectionTester.unorderedReferencesEqual(tree.search(area), expected));
            }
        }
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
