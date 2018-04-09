package net.chaosworship.topuslib.graph;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.tuple.IntPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "WeakerAccess", "unused"})
@SuppressLint("UseSparseArrays")
public class HashSimpleGraph implements SimpleGraph {

    private int mNextVertex;

    // the keys here are the vertex collection
    // every edge is represented twice
    final HashMap<Integer, HashSet<Integer>> mNeighborSets;

    public HashSimpleGraph() {
        mNextVertex = 1;
        mNeighborSets = new HashMap<>();
    }

    @Override
    public void clear() {
        mNextVertex = 1;
        mNeighborSets.clear();
    }

    @Override
    public int addVertex() {
        int vertex = mNextVertex++;
        while(mNeighborSets.containsKey(vertex)) {
            vertex = mNextVertex++;
            if (vertex < 0) {
                throw new AssertionError();
            }
        }
        addVertex(vertex);
        return vertex;
    }

    @Override
    public void addVertex(int vertex) {
        if(mNeighborSets.containsKey(vertex)) {
            throw new IllegalStateException("already have that vertex");
        }
        mNeighborSets.put(vertex, new HashSet<Integer>());
    }

    @Override
    public void removeVertex(int vertex) {
        for(int neighbor : getNeighbors(vertex)) {
            mNeighborSets.get(neighbor).remove(vertex);
        }
        mNeighborSets.remove(vertex);
    }

    @Override
    public Set<Integer> getVertices() {
        return mNeighborSets.keySet();
    }

    @Override
    public Set<Integer> getNeighbors(int vertex) {
        Set<Integer> neighbors = mNeighborSets.get(vertex);
        if(neighbors == null) {
            throw new IllegalStateException("no such vertex");
        }
        return neighbors;
    }

    @Override
    public void addEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException("no loops");
        }
        if(hasEdge(a, b)) {
            throw new IllegalStateException("already have that edge");
        }
        mNeighborSets.get(a).add(b);
        mNeighborSets.get(b).add(a);
    }

    @Override
    public boolean tryAddEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException("no loops");
        }
        boolean newEdge = !hasEdge(a, b);
        if(newEdge) {
            mNeighborSets.get(a).add(b);
            mNeighborSets.get(b).add(a);
        }
        return newEdge;
    }

    @Override
    public void removeEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException();
        }
        if(!hasEdge(a, b)) {
            throw new IllegalStateException("no such edge");
        }
        mNeighborSets.get(a).remove(b);
        mNeighborSets.get(b).remove(a);
    }

    @Override
    public boolean hasVertex(int vertex) {
        return mNeighborSets.containsKey(vertex);
    }

    @Override
    public boolean hasEdge(int a, int b) {
        HashSet<Integer> aNeighbors = mNeighborSets.get(a);
        HashSet<Integer> bNeighbors = mNeighborSets.get(b);
        if(aNeighbors == null || bNeighbors == null) {
            throw new IllegalStateException("no such vertex");
        }
        boolean hasNeighbor = aNeighbors.contains(b);
        if(BuildConfig.DEBUG) {
            if(bNeighbors.contains(a) != hasNeighbor) {
                throw new AssertionError();
            }
        }
        return hasNeighbor;
    }

    @Override
    public ArrayList<IntPair> getEdges() {
        ArrayList<IntPair> edges = new ArrayList<>();
        for(int a : mNeighborSets.keySet()) {
            for(int b : mNeighborSets.get(a)) {
                if(a < b) {
                    edges.add(new IntPair(a, b));
                }
            }
        }
        return edges;
    }

    @Override
    public void putEdges(IntPairConsumer consumer) {
        for(int a : mNeighborSets.keySet()) {
            for(int b : mNeighborSets.get(a)) {
                if(a < b) {
                    consumer.putIntPair(a, b);
                }
            }
        }
    }

    @Override
    public void putIntPair(int a, int b) {
        addEdge(a, b);
    }

    public static HashSimpleGraph generateCompleteGraph(int vertexCount) {
        HashSimpleGraph g = new HashSimpleGraph();
        ArrayList<Integer> vertices = new ArrayList<>();
        for(int i = 0; i < vertexCount; i++) {
            Integer v = g.addVertex();
            for(Integer u : vertices) {
                g.addEdge(v, u);
            }
            vertices.add(v);
        }
        return g;
    }
}
