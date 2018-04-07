package net.chaosworship.topuslib;

import net.chaosworship.topuslib.graph.HashSimpleGraph;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.*;


public class HashSimpleGraphTest {

    @Test
    public void emptyGraph() {
        HashSimpleGraph g = new HashSimpleGraph();
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
        HashSimpleGraph g = new HashSimpleGraph();
        int a = g.addVertex();
        try {
            g.addEdge(a, a);
            fail();
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void edges() {
        HashSimpleGraph g = new HashSimpleGraph();
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
        HashSimpleGraph g = new HashSimpleGraph();
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

    @Test
    public void completeGraph() {
        HashSimpleGraph g;
        g = HashSimpleGraph.generateCompleteGraph(0);
        assertTrue(g.getVertices().isEmpty());
        g = HashSimpleGraph.generateCompleteGraph(1);
        int v = g.getVertices().iterator().next();
        assertTrue(g.getNeighbors(v).isEmpty());
        final int n = 7;
        g = HashSimpleGraph.generateCompleteGraph(n);
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
}
