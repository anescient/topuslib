package net.chaosworship.topuslibtest.benchmark;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.mesh.DelaunayTriangulator;
import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.graph.MatrixSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;


public class DelaunayBench extends TimedRunner implements IntPairConsumer {

    private static final SuperRandom sRandom = new SuperRandom(771);

    public void run() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        SimpleGraph graph = new MatrixSimpleGraph();
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            points.add(randomPoint());
        }
        for(int i = 0; i < 400; i++) {
            points.add(randomPoint());
            try {
                triangulator.triangulate(points);
            } catch (DelaunayTriangulator.NumericalFailure ignored) {}

            //triangulator.getEdgeGraph(graph);
        }
    }

    private static Vec2 randomPoint() {
        return new Vec2(sRandom.nextFloat(), sRandom.nextFloat());
    }

    @Override
    public void addIntPair(int a, int b) {

    }
}
