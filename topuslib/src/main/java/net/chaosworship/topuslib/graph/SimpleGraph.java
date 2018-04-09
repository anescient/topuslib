package net.chaosworship.topuslib.graph;

import net.chaosworship.topuslib.tuple.IntPair;

import java.util.ArrayList;
import java.util.Set;


public interface SimpleGraph extends GraphEdgeConsumer {

    void clear();

    int addVertex();

    void addVertex(int vertex);

    void removeVertex(int vertex);

    Set<Integer> getVertices();

    Set<Integer> getNeighbors(int vertex);

    void addEdge(int a, int b);

    boolean tryAddEdge(int a, int b);

    void removeEdge(int a, int b);

    boolean hasVertex(int vertex);

    boolean hasEdge(int a, int b);

    ArrayList<IntPair> getEdges();

    void putEdges(GraphEdgeConsumer consumer);

}
