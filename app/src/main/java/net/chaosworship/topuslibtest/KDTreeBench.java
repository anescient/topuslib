package net.chaosworship.topuslibtest;

import android.os.SystemClock;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.rangesearch.PointValuePair;
import net.chaosworship.topuslib.geom2d.rangesearch.RectangularSearch;

import java.util.ArrayList;
import java.util.Random;


public class KDTreeBench {
    private static final int POINTCOUNT = 2000;
    private static final int SEARCHCOUNT = 300;
    private static final int ROUNDCOUNT = 2000;

    private static final Random sRandom = new Random(1234);

    ArrayList<PointValuePair<String>> mPointValues;
    ArrayList<Rectangle> mSearchAreas;

    KDTreeBench() {
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
            float maxx = minx + sRandom.nextFloat() * 0.02f;
            float maxy = miny + sRandom.nextFloat() * 0.02f;
            mSearchAreas.add(new Rectangle(minx, miny, maxx, maxy));
        }
    }

    // return ms
    long timedTest(RectangularSearch<String> search) {
        long start = SystemClock.uptimeMillis();
        for(int round = 0; round < ROUNDCOUNT; round++) {
            search.load(mPointValues);
            for(Rectangle area : mSearchAreas) {
                search.search(area);
            }
        }
        return SystemClock.uptimeMillis() - start;
    }
}
