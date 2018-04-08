package net.chaosworship.topuslib.graph;

import net.chaosworship.topuslib.tuple.IntPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;


public class MatrixSimpleGraph implements SimpleGraph {

    private int mNewVertex;
    private final BitSet mVertices;
    private BitSet[] mNeighborSets;


    public MatrixSimpleGraph() {
        mNewVertex = 0;
        mVertices = new BitSet();
        mNeighborSets = new BitSet[0];
    }

    @Override
    public void clear() {
        mNewVertex = 0;
        mVertices.clear();
        for(BitSet neighbors : mNeighborSets) {
            neighbors.clear();
        }
    }

    @Override
    public int addVertex() {
        mNewVertex = mVertices.nextClearBit(mNewVertex);
        addVertex(mNewVertex);
        return mNewVertex;
    }

    @Override
    public void addVertex(int vertex) {
        if(vertex < 0) {
            throw new IllegalArgumentException();
        }
        if(mVertices.get(vertex)) {
            throw new IllegalStateException("already have that vertex");
        }
        mVertices.set(vertex);
        if(vertex >= mNeighborSets.length) {
            int oldSize = mNeighborSets.length;
            int newSize = vertex + 1;
            mNeighborSets = Arrays.copyOf(mNeighborSets, newSize);
            for(int i = oldSize; i < newSize; i++) {
                mNeighborSets[i] = new BitSet();
            }
        }
        if(!mNeighborSets[vertex].isEmpty()) {
            throw new AssertionError();
        }
    }

    @Override
    public void removeVertex(int vertex) {
        if(!mVertices.get(vertex)) {
            throw new IllegalStateException("no such vertex");
        }
        mVertices.clear(vertex);
        BitSet neighborSet = mNeighborSets[vertex];
        int neighbor = neighborSet.nextSetBit(0);
        while(neighbor >= 0) {
            mNeighborSets[neighbor].clear(vertex);
            neighbor = neighborSet.nextSetBit(neighbor + 1);
        }
        neighborSet.clear();
    }

    @Override
    public Set<Integer> getVertices() {
        HashSet<Integer> vertices = new HashSet<>();
        int vertex = mVertices.nextSetBit(0);
        while(vertex >= 0) {
            vertices.add(vertex);
            vertex = mVertices.nextSetBit(vertex + 1);
        }
        return vertices;
    }

    @Override
    public Set<Integer> getNeighbors(int vertex) {
        if(!mVertices.get(vertex)) {
            throw new IllegalStateException("no such vertex");
        }
        HashSet<Integer> vertices = new HashSet<>();
        BitSet neighborSet = mNeighborSets[vertex];
        int neighbor = neighborSet.nextSetBit(0);
        while(neighbor >= 0) {
            vertices.add(neighbor);
            neighbor = neighborSet.nextSetBit(neighbor + 1);
        }
        return vertices;
    }

    @Override
    public void addEdge(int a, int b) {
        if(a == b) {
            throw new IllegalArgumentException("no loops");
        }
        if(!mVertices.get(a) || !mVertices.get(b)) {
            throw new IllegalStateException("no such vertex(es)");
        }
        if(mNeighborSets[a].get(b)) {
            throw new IllegalStateException("already have that edge");
        }
        mNeighborSets[a].set(b);
        mNeighborSets[b].set(a);
    }

    @Override
    public boolean tryAddEdge(int a, int b) {
        boolean newEdge = !hasEdge(a, b);
        if(newEdge) {
            addEdge(a, b);
        }
        return newEdge;
    }

    @Override
    public void removeEdge(int a, int b) {
        if(!hasEdge(a, b)) {
            throw new IllegalStateException("no such edge");
        }
        mNeighborSets[a].clear(b);
        mNeighborSets[b].clear(a);
    }

    @Override
    public boolean hasVertex(int vertex) {
        return mVertices.get(vertex);
    }

    @Override
    public boolean hasEdge(int a, int b) {
        if(!mVertices.get(a) || !mVertices.get(b)) {
            throw new IllegalStateException("no such vertex(es)");
        }
        return mNeighborSets[a].get(b);
    }

    @Override
    public ArrayList<IntPair> getEdges() {
        ArrayList<IntPair> edges = new ArrayList<>();
        int a = mVertices.nextSetBit(0);
        while(a >= 0) {
            BitSet aNeighbors = mNeighborSets[a];
            int b = aNeighbors.nextSetBit(a + 1);
            while(b >= 0) {
                edges.add(new IntPair(a, b));
                b = aNeighbors.nextSetBit(b + 1);
            }
            a = mVertices.nextSetBit(a + 1);
        }
        return edges;
    }

    @Override
    public void putEdges(GraphEdgeConsumer consumer) {
        int a = mVertices.nextSetBit(0);
        while(a >= 0) {
            BitSet aNeighbors = mNeighborSets[a];
            int b = aNeighbors.nextSetBit(a + 1);
            while(b >= 0) {
                consumer.putGraphEdge(a, b);
                b = aNeighbors.nextSetBit(b + 1);
            }
            a = mVertices.nextSetBit(a + 1);
        }
    }
}
