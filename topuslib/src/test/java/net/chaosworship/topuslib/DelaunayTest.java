package net.chaosworship.topuslib;

import net.chaosworship.topuslib.geom2d.Circle;
import net.chaosworship.topuslib.geom2d.Circumcircle;
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

import static junit.framework.Assert.*;


public class DelaunayTest {

    @Test
    public void edgeEnumeration() {
        SuperRandom random = new SuperRandom(1234);
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        ArrayList<Vec2> points = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            points.add(new Vec2(random.nextFloat(), random.nextFloat()));
        }
        for(int i = 0; i < 20; i++) {
            points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            Triangulation triangulation = triangulator.triangulate(points);

            /*
            HashSet<IntPair> edgeSetA = new HashSet<>();
            Triangulation triangulation = triangulator.getTriangulation();
            for(IntPair ip : triangulation.getEdges()) {
                edgeSetA.add(IntPair.sorted(ip.a, ip.b));
            }
*/

            HashSet<IntPair> edgeSetB = new HashSet<>();
            SimpleGraph graph = new HashSimpleGraph();
            triangulator.getEdgeGraph(graph);
            for(IntPair ip : graph.getEdges()) {
                edgeSetB.add(IntPair.sorted(ip.a, ip.b));
            }

            final HashSet<IntPair> edgeSetC = new HashSet<>();
            IntPairConsumer consumer = new IntPairConsumer() {
                @Override
                public void addIntPair(int a, int b) {
                    edgeSetC.add(IntPair.sorted(a, b));
                }
            };
            triangulation.outputEdges(consumer);

//            assertTrue(CollectionTester.intPairSetsEqual(edgeSetA, edgeSetB));
            assertTrue(CollectionTester.intPairSetsEqual(edgeSetB, edgeSetC));
        }
    }

    @Test
    public void isDelaunay() {
        SuperRandom random = new SuperRandom(1234);
        ArrayList<Vec2> points = new ArrayList<>();
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        for(int pointCount = 3; pointCount < 50; pointCount++) {
            points.clear();
            for(int i = 0; i < pointCount; i++) {
                points.add(new Vec2(random.nextFloat(), random.nextFloat()));
            }
            triangulator.triangulate(points);
            assertDelaunny(points, triangulator.getTriangles());
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
