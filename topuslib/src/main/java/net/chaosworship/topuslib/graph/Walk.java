package net.chaosworship.topuslib.graph;

import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;
import java.util.HashSet;


public class Walk {

    private static final SuperRandom sRandom = new SuperRandom();

    private Walk() {}

    public static ArrayList<Integer> randomWalk(SimpleGraph graph) {
        ArrayList<Integer> path = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();
        HashSet<Integer> nexts = new HashSet<>();
        nexts.add(sRandom.choice(graph.getVertices()));
        while(!nexts.isEmpty()) {
            Integer next = sRandom.choice(nexts);
            path.add(next);
            visited.add(next);
            nexts.clear();
            nexts.addAll(graph.getNeighbors(next));
            nexts.removeAll(visited);
        }
        return path;
    }
}
