package net.chaosworship.topuslib;

import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.tuple.IntPair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static junit.framework.Assert.*;


public abstract class SimpleGraphTest {

    abstract SimpleGraph getGraph();

    @Test
    public void emptyGraph() {
        SimpleGraph g = getGraph();
        try {
            g.hasEdge(1, 2);
            fail();
        } catch (IllegalStateException ignored) {}
        try {
            g.removeEdge(1, 2);
            fail();
        } catch (IllegalStateException ignored) {}
        try {
            g.removeVertex(1);
            fail();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void noLoops() {
        SimpleGraph g = getGraph();
        int a = g.addVertex();
        try {
            g.addEdge(a, a);
            fail();
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void edges() {
        SimpleGraph g = getGraph();
        int a = g.addVertex();
        ArrayList<Integer> bbb = new ArrayList<>();
        bbb.add(g.addVertex());
        bbb.add(g.addVertex());
        bbb.add(g.addVertex());
        assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(a), new ArrayList<Integer>()));
        for(Integer b : bbb) {
            assertFalse(g.hasEdge(a, b));
            assertFalse(g.hasEdge(b, a));
        }
        g.addEdge(a, bbb.get(0));
        try {
            g.addEdge(a, bbb.get(0));
            fail();
        } catch (IllegalStateException ignored) {}
        assertFalse(g.tryAddEdge(a, bbb.get(0)));
        assertTrue(g.tryAddEdge(a, bbb.get(1)));
        g.addEdge(a, bbb.get(2));
        assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(a), bbb));
        ArrayList<Integer> aSingle = new ArrayList<>();
        aSingle.add(a);
        for(Integer b : bbb) {
            assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(b), aSingle));
            assertTrue(g.hasEdge(a, b));
            assertTrue(g.hasEdge(b, a));
        }
        for(Integer b : bbb) {
            g.removeEdge(a, b);
            try {
                g.removeEdge(a, b);
                fail();
            } catch (IllegalStateException ignored) {}
        }
        assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(a), new ArrayList<Integer>()));
        for(Integer b : bbb) {
            assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(b), new ArrayList<Integer>()));
        }
        assertFalse(CollectionTester.intSetsEqual(g.getNeighbors(a), bbb));
        g.clear();
        try {
            g.hasEdge(a, bbb.get(0));
            fail();
        } catch (IllegalStateException ignored) {}
    }

    @Test
    public void removeVertex() {
        SimpleGraph g = getGraph();
        int a = g.addVertex();
        int b = g.addVertex();
        int c = g.addVertex();
        int d = g.addVertex();
        int e = g.addVertex();
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(a);
        expected.add(b);
        expected.add(c);
        expected.add(d);
        expected.add(e);
        assertTrue(CollectionTester.intSetsEqual(expected, g.getVertices()));
        g.addEdge(a, b);
        g.addEdge(a, c);
        g.addEdge(a, d);
        g.addEdge(d, b);
        g.addEdge(d, e);
        g.addEdge(c, b);
        assertTrue(CollectionTester.intPairSetsEqual(g.getEdges(), consumeEdges(g)));
        expected.clear();
        expected.add(b);
        expected.add(c);
        expected.add(d);
        assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(a), expected));
        assertTrue(g.hasEdge(a, b));
        assertTrue(g.hasEdge(d, b));
        assertTrue(g.hasEdge(c, b));
        g.removeVertex(b);
        try {
            g.getNeighbors(b);
            fail();
        } catch (IllegalStateException ignored) {}
        expected.clear();
        expected.add(c);
        expected.add(d);
        assertTrue(CollectionTester.intSetsEqual(g.getNeighbors(a), expected));
    }

    private static HashSet<IntPair> consumeEdges(SimpleGraph g) {
        final HashSet<IntPair> pairs = new HashSet<>();
        IntPairConsumer consumer = new IntPairConsumer() {
            @Override
            public void addIntPair(int a, int b) {
                pairs.add(new IntPair(a, b));
            }
        };
        g.outputEdges(consumer);
        return pairs;
    }

    /*
      maybe generateCompleteGraph could be implemented in an abstract base

    @Test
    public void completeGraph() {
        SimpleGraph g;
        g = SimpleGraph.generateCompleteGraph(0);
        assertTrue(g.getVertices().isEmpty());
        g = SimpleGraph.generateCompleteGraph(1);
        int v = g.getVertices().iterator().next();
        assertTrue(g.getNeighbors(v).isEmpty());
        final int n = 7;
        g = SimpleGraph.generateCompleteGraph(n);
        for(int a : g.getVertices()) {
            assertTrue(g.getNeighbors(a).size() == n - 1);
        }
        for(int a : g.getVertices()) {
            for(int b : g.getVertices()) {
                if(a != b) {
                    assertTrue(g.hasEdge(a, b));
                    assertTrue(g.hasEdge(b, a));
                }
            }
        }
    }
    */
}
