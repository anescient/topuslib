package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.SegmentIntersection;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.geom2d.mesh.DelaunayTriangulator;
import net.chaosworship.topuslib.geom2d.mesh.Triangulation;
import net.chaosworship.topuslib.collection.IntPairConsumer;
import net.chaosworship.topuslib.graph.HashSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.random.SuperRandom;
import net.chaosworship.topuslib.tuple.IntPair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.*;


public class DelaunayTest {

    private static class EdgeSetLoader implements IntPairConsumer {
        HashSet<IntPair> mPairSet;
        int mCount;
        EdgeSetLoader(HashSet<IntPair> pairSet) {
            mPairSet = pairSet;
            mCount = 0;
        }
        @Override
        public void addIntPair(int a, int b) {
            mCount++;
            assertFalse(a == b);
            IntPair pair = IntPair.sorted(a, b);
            assertFalse(mPairSet.contains(pair));
            mPairSet.add(pair);
        }
    }

    @Test
    public void edgeEnumeration() throws DelaunayTriangulator.NumericalFailure {
        SuperRandom random = new SuperRandom(1234);
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            points.add(new Vec2(random.nextFloat(), random.nextFloat()));
        }
        for(int i = 0; i < 20; i++) {
            points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            triangulator.triangulate(points);
            Triangulation triangulation = triangulator.getTriangulation();

            HashSet<IntPair> edgeSetA = new HashSet<>();
            EdgeSetLoader setLoader = new EdgeSetLoader(edgeSetA);
            triangulator.outputDelaunayEdges(setLoader);

            HashSet<IntPair> edgeSetB = new HashSet<>();
            SimpleGraph graph = new HashSimpleGraph();
            triangulator.getEdgeGraph(graph);
            for(IntPair ip : graph.getEdges()) {
                edgeSetB.add(IntPair.sorted(ip.a, ip.b));
            }

            final HashSet<IntPair> edgeSetC = new HashSet<>();
            setLoader = new EdgeSetLoader(edgeSetC);
            triangulation.outputEdges(setLoader);

            assertTrue(CollectionTester.intPairSetsEqual(edgeSetA, edgeSetB));
            assertTrue(CollectionTester.intPairSetsEqual(edgeSetB, edgeSetC));
        }
    }

    @Test
    public void isDelaunay() throws DelaunayTriangulator.NumericalFailure {
        SuperRandom random = new SuperRandom(1234);
        ArrayList<Vec2> points = new ArrayList<>();
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        for(int pointCount = 3; pointCount < 50; pointCount++) {
            points.clear();
            for(int i = 0; i < pointCount; i++) {
                points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            }
            triangulator.triangulate(points);
            assertTriangulation(triangulator.getTriangulation(), points);
            assertDelaunny(points, triangulator.getTriangles());
        }
    }

    private static void assertTriangulation(Triangulation triangulation, List<Vec2> points) {
        final HashSet<IntPair> pairSet = new HashSet<>();
        IntPairConsumer setFiller = new IntPairConsumer() {
            @Override
            public void addIntPair(int a, int b) {
                IntPair pair = new IntPair(a, b);
                assertFalse(pairSet.contains(pair));
                pairSet.add(pair);
            }
        };
        triangulation.outputEdges(setFiller);
        ArrayList<IntPair> pairList = new ArrayList<>(pairSet);
        for(int i = 0; i < pairList.size(); i++) {
            IntPair pi = pairList.get(i);
            for(int j = i + 1; j < pairList.size(); j++) {
                IntPair pj = pairList.get(j);
                Vec2 a = points.get(pi.a);
                Vec2 b = points.get(pi.b);
                Vec2 c = points.get(pj.a);
                Vec2 d = points.get(pj.b);
                SegmentIntersection intersection = new SegmentIntersection(a, b, c, d);
                assertFalse(intersection.segmentsIntersect() && !SegmentIntersection.connected(a, b, c, d));
            }
        }
    }

    private static void assertDelaunny(Collection<Vec2> points, Collection<Triangle> triangles) {
        assertTrue(triangles.size() >= points.size() - 2);
        for(Triangle t : triangles) {
            for(Triangle tt : triangles) {
                assertTrue(t == tt || !t.equals(tt));
            }
            Circle c = Circumcircle.toCircle(t);
            c.radius *= 0.99999f;
            for(Vec2 p : points) {
                assertFalse(c.contains(p));
            }
        }
    }
}
