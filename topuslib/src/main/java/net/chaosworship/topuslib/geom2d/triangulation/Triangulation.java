package net.chaosworship.topuslib.geom2d.triangulation;

import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.collection.IntPairList;
import net.chaosworship.topuslib.collection.SegmentConsumer;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.ArrayList;


@SuppressWarnings("unused")
public class Triangulation {

    private class SegmentResolver implements IntPairConsumer {
        SegmentConsumer segmentConsumer;
        @Override
        public void addIntPair(int a, int b) {
            segmentConsumer.addSegment(mPoints[a], mPoints[b]);
        }
    }

    private Vec2[] mPoints;
    private final IntPairList mEdges;
    private final ArrayList<Triangle> mTriangles;
    private SegmentResolver mSegmentResolver;

    Triangulation() {
        mPoints = null;
        mEdges = new IntPairList();
        mTriangles = new ArrayList<>();
        mSegmentResolver = new SegmentResolver();
    }

    void init(Vec2[] points) {
        mPoints = points;
        mEdges.clear();
        mTriangles.clear();
    }

    void addEdge(int a, int b) {
        mEdges.add(a, b);
    }

    void addTriangle(Triangle triangle) {
        mTriangles.add(triangle);
    }

    public void outputEdges(IntPairConsumer consumer) {
        mEdges.outputPairs(consumer);
    }

    public void outputSegments(SegmentConsumer consumer) {
        mSegmentResolver.segmentConsumer = consumer;
        mEdges.outputPairs(mSegmentResolver);
    }

    public Iterable<Triangle> getTriangles() {
        return mTriangles;
    }
}
