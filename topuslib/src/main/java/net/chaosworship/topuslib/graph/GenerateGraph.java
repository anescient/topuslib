package net.chaosworship.topuslib.graph;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.collection.CuboidMap;
import net.chaosworship.topuslib.tuple.IntTriple;

import java.util.HashMap;
import java.util.Map;


public class GenerateGraph {

    private GenerateGraph() {}

    // add (width * height * depth) vertices to graph
    // add edges to form a complete 3D grid
    // return mapping of vertex to grid position
    // grid positions are a:[0,width), b:[0, height), c:[0, depth)
    public static Map<Integer, IntTriple> cuboidGrid(SimpleGraph graph, int width, int height, int depth) {
        if(width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException();
        }
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, IntTriple> vertexMap = new HashMap<>();
        CuboidMap<Integer> vertexUnMap = new CuboidMap<>(width, height, depth);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                for(int z = 0; z < depth; z++) {
                    int vertex = graph.addVertex();
                    vertexMap.put(vertex, new IntTriple(x, y, z));
                    vertexUnMap.set(x, y, z, vertex);

                    if(x > 0) {
                        graph.addEdge(vertex, vertexUnMap.get(x - 1, y, z));
                    }
                    if(y > 0) {
                        graph.addEdge(vertex, vertexUnMap.get(x, y - 1, z));
                    }
                    if(z > 0) {
                        graph.addEdge(vertex, vertexUnMap.get(x, y, z - 1));
                    }
                }
            }
        }
        return vertexMap;
    }
}
