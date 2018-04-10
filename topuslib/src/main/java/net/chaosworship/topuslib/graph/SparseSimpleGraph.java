package net.chaosworship.topuslib.graph;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.tuple.IntPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class SparseSimpleGraph implements SimpleGraph {

    private int mNextVertex;

    // the keys here are the vertex collection
    // every edge is represented twice
    private final SparseArray<SparseBooleanArray> mNeighborSets;

    public SparseSimpleGraph() {
        mNextVertex = 1;
        mNeighborSets = new SparseArray<>();
    }

    public void clear() {
        mNextVertex = 1;
        mNeighborSets.clear();
    }

    public int addVertex() {
        int vertex = mNextVertex++;
        while(mNeighborSets.indexOfKey(vertex) >= 0) {
            vertex = mNextVertex++;
            if (vertex < 0) {
                throw new AssertionError();
            }
        }
        addVertex(vertex);
        return vertex;
    }

    public void addVertex(int vertex) {
        if(mNeighborSets.indexOfKey(vertex) >= 0) {
            throw new IllegalStateException("already have that vertex");
        }
        mNeighborSets.put(vertex, new SparseBooleanArray());
    }

    public void tryAddVertex(int vertex) {
        if(!hasVertex(vertex)) {
            addVertex(vertex);
        }
    }

    public void removeVertex(int vertex) {
        for(int neighbor : getNeighbors(vertex)) {
            mNeighborSets.get(neighbor).delete(vertex);
        }
        mNeighborSets.remove(vertex);
    }

    public Set<Integer> getVertices() {
        HashSet<Integer> vertices = new HashSet<>();
        for(int i = 0; i < mNeighborSets.size(); i++) {
            vertices.add(mNeighborSets.keyAt(i));
        }
        return vertices;
    }

    public Set<Integer> getNeighbors(int vertex) {
        HashSet<Integer> neighborList = new HashSet<>();
        SparseBooleanArray neighbors = mNeighborSets.get(vertex);
        if(neighbors == null) {
            throw new IllegalStateException("no such vertex");
        }
        for(int i = 0; i < neighbors.size(); i++) {
            neighborList.add(neighbors.keyAt(i));
        }
        return neighborList;
    }

    public void addEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException("no loops");
        }
        if(hasEdge(a, b)) {
            throw new IllegalStateException("already have that edge");
        }
        mNeighborSets.get(a).append(b, true);
        mNeighborSets.get(b).append(a, true);
    }

    public boolean tryAddEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException("no loops");
        }
        boolean newEdge = !hasEdge(a, b);
        if(newEdge) {
            tryAddVertex(a);
            tryAddVertex(b);
            mNeighborSets.get(a).append(b, true);
            mNeighborSets.get(b).append(a, true);
        }
        return newEdge;
    }

    public void removeEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException();
        }
        if(!hasEdge(a, b)) {
            throw new IllegalStateException("no such edge");
        }
        mNeighborSets.get(a).delete(b);
        mNeighborSets.get(b).delete(a);
    }

    public boolean hasVertex(int vertex) {
        return mNeighborSets.indexOfKey(vertex) >= 0;
    }

    public boolean hasEdge(int a, int b) {
        SparseBooleanArray aNeighbors = mNeighborSets.get(a);
        return aNeighbors != null && aNeighbors.indexOfKey(b) >= 0;
    }

    public ArrayList<IntPair> getEdges() {
        ArrayList<IntPair> edges = new ArrayList<>();
        for(int i = 0; i < mNeighborSets.size(); i++) {
            int a = mNeighborSets.keyAt(i);
            SparseBooleanArray aNeighbors = mNeighborSets.valueAt(i);
            for(int j = 0; j < aNeighbors.size(); j++) {
                int b = aNeighbors.keyAt(j);
                if(a < b) {
                    edges.add(new IntPair(a, b));
                }
            }
        }
        return edges;
    }

    @Override
    public void putEdges(IntPairConsumer consumer) {
        for(int i = 0; i < mNeighborSets.size(); i++) {
            int a = mNeighborSets.keyAt(i);
            SparseBooleanArray aNeighbors = mNeighborSets.valueAt(i);
            for(int j = 0; j < aNeighbors.size(); j++) {
                int b = aNeighbors.keyAt(j);
                if(a < b) {
                    consumer.addIntPair(a, b);
                }
            }
        }
    }

    public static SimpleGraph generateCompleteGraph(int vertexCount) {
        SimpleGraph g = new SparseSimpleGraph();
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

    @Override
    public void addIntPair(int a, int b) {
        addEdge(a, b);
    }
}
