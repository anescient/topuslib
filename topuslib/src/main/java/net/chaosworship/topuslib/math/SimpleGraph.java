package net.chaosworship.topuslib.math;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.BuildConfig;

import java.util.HashMap;
import java.util.HashSet;


@SuppressLint("UseSparseArrays")
public class SimpleGraph {

    private int mNextVertex;

    // the keys here are the vertex collection
    // every edge is represented twice
    private final HashMap<Integer, HashSet<Integer>> mNeighborSets;

    public SimpleGraph() {
        mNextVertex = 1;
        mNeighborSets = new HashMap<>();
    }

    public void clear() {
        mNextVertex = 1;
        mNeighborSets.clear();
    }

    public int addVertex() {
        int vertex = mNextVertex++;
        if(vertex < 0) {
            throw new AssertionError();
        }
        mNeighborSets.put(vertex, new HashSet<Integer>());
        return vertex;
    }

    public void removeVertex(int vertex) {
        for(int neighbor : getNeighbors(vertex)) {
            mNeighborSets.get(neighbor).remove(vertex);
        }
        mNeighborSets.remove(vertex);
    }

    public Iterable<Integer> getVertices() {
        return mNeighborSets.keySet();
    }

    public Iterable<Integer> getNeighbors(int vertex) {
        HashSet<Integer> neighborSet = mNeighborSets.get(vertex);
        if(neighborSet == null) {
            throw new IllegalStateException("no such vertex");
        }
        return neighborSet;
    }

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
}
