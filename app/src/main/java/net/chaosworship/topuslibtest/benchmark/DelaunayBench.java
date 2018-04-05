package net.chaosworship.topuslibtest.benchmark;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.triangulation.DelaunayTriangulation;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;


public class DelaunayBench extends TimedRunner {

    private static final SuperRandom sRandom = new SuperRandom();

    public void run() {
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            points.add(randomPoint());
        }
        for(int i = 0; i < 400; i++) {
            points.add(randomPoint());
            new DelaunayTriangulation(points).getTriangles();
        }
    }

    private static Vec2 randomPoint() {
        return new Vec2(sRandom.nextFloat(), sRandom.nextFloat());
    }
}
