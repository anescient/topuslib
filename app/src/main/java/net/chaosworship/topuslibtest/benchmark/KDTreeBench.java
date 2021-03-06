package net.chaosworship.topuslibtest.benchmark;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.rangesearch.KDTree;
import net.chaosworship.topuslib.tuple.PointValuePair;

import java.util.ArrayList;
import java.util.Random;


public class KDTreeBench extends TimedRunner {
    private static final int POINTCOUNT = 7000;
    private static final int SEARCHCOUNT = 10000;
    private static final int ROUNDCOUNT = 30;

    private static final Random sRandom = new Random(1234);

    private ArrayList<PointValuePair<String>> mPointValues;
    private ArrayList<Rectangle> mSearchAreas;
    private KDTree<String> mSearch;

    public KDTreeBench() {
        mPointValues = new ArrayList<>();
        for(int i = 0; i < POINTCOUNT; i++) {
            mPointValues.add(new PointValuePair<>(
                    new Vec2(sRandom.nextFloat(), sRandom.nextFloat()),
                    Integer.toString(sRandom.nextInt())));
        }

        mSearchAreas = new ArrayList<>();
        for(int i = 0; i < SEARCHCOUNT; i++) {
            float minx = sRandom.nextFloat();
            float miny = sRandom.nextFloat();
            float maxx = minx + sRandom.nextFloat() * 0.1f;
            float maxy = miny + sRandom.nextFloat() * 0.1f;
            mSearchAreas.add(new Rectangle(minx, miny, maxx, maxy));
        }

        mSearch = new KDTree<>();
    }

    public void run() {
        for(int round = 0; round < ROUNDCOUNT; round++) {
            mSearch.load(mPointValues);
            for(Rectangle area : mSearchAreas) {
                mSearch.search(area);
            }
        }
    }
}
