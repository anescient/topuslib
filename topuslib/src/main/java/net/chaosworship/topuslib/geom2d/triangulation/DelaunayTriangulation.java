package net.chaosworship.topuslib.geom2d.triangulation;

import net.chaosworship.topuslib.IntTriple;
import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;
import java.util.Collection;


public class DelaunayTriangulation {

    ////////////////////////////////////////////////

    private class TriangleNode {

        private final IntTriple vertices;
        private Triangle triangle;
        private final TriangleNode[] children;
        private TriangleNode adjacentAB; // node with triangle sharing edge AB
        private TriangleNode adjacentBC;
        private TriangleNode adjacentCA;

        private TriangleNode(int a, int b, int c) {
            vertices = new IntTriple(a, b, c);
            triangle = new Triangle(
                    mPoints[vertices.a],
                    mPoints[vertices.b],
                    mPoints[vertices.c]);
            children = new TriangleNode[3];
            children[0] = null;
            children[1] = null;
            children[2] = null;
            adjacentAB = null;
            adjacentBC = null;
            adjacentCA = null;
        }

        private void validateAdjacent() {
            if(adjacentAB != null) {
                if(adjacentHavingEdge(vertices.a, vertices.b) != adjacentAB)
                    throw new AssertionError();
                if(adjacentAB.adjacentHavingEdge(vertices.a, vertices.b) != this)
                    throw new AssertionError();
            }
            if(adjacentBC != null) {
                if(adjacentHavingEdge(vertices.b, vertices.c) != adjacentBC)
                    throw new AssertionError();
                if(adjacentBC.adjacentHavingEdge(vertices.b, vertices.c) != this)
                    throw new AssertionError();
            }
            if(adjacentCA != null) {
                if(adjacentHavingEdge(vertices.a, vertices.c) != adjacentCA)
                    throw new AssertionError();
                if(adjacentCA.adjacentHavingEdge(vertices.a, vertices.c) != this)
                    throw new AssertionError();
            }
            for(TriangleNode child : children) {
                if(child != null)
                    child.validateAdjacent();
            }
        }

        private boolean isLeaf() {
            return children[0] == null; // implies other children are null, too
        }

        private boolean contains(int pi) {
            Vec2 p = mPoints[pi];
            return triangle.contains(p);
        }

        private void getLeafTriangles(Collection<Triangle> triangles, int maxVertex) {
            if(isLeaf()) {
                if(!vertices.includesAnyOver(maxVertex)) {
                    triangles.add(triangle);
                }
            } else {
                for(int childi = 0; childi < 3; childi++) {
                    TriangleNode child = children[childi];
                    if(child != null) {
                        child.getLeafTriangles(triangles, maxVertex);
                    }
                }
            }
        }

        private void replaceAdjacent(TriangleNode was, TriangleNode is) {
            if(adjacentAB == was) {
                adjacentAB = is;
            } else if(adjacentBC == was) {
                adjacentBC = is;
            } else if(adjacentCA == was) {
                adjacentCA = is;
            } else {
                throw new AssertionError();
            }
        }

        private TriangleNode insertPoint(int pr) {
            if(isLeaf()) {
                TriangleNode childAB = children[0] = new TriangleNode(vertices.a, vertices.b, pr);
                TriangleNode childBC = children[1] = new TriangleNode(pr, vertices.b, vertices.c);
                TriangleNode childCA = children[2] = new TriangleNode(vertices.a, pr, vertices.c);

                if(adjacentAB != null) {
                    adjacentAB.replaceAdjacent(this, childAB);
                }
                if(adjacentBC != null) {
                    adjacentBC.replaceAdjacent(this, childBC);
                }
                if(adjacentCA != null) {
                    adjacentCA.replaceAdjacent(this, childCA);
                }

                childAB.adjacentAB = adjacentAB;
                childAB.adjacentBC = childBC;
                childAB.adjacentCA = childCA;

                childBC.adjacentAB = childAB;
                childBC.adjacentBC = adjacentBC;
                childBC.adjacentCA = childCA;

                childCA.adjacentAB = childAB;
                childCA.adjacentBC = childBC;
                childCA.adjacentCA = adjacentCA;

                if(adjacentAB != null) {
                    childAB.legalizeEdge(pr, vertices.a, vertices.b, adjacentAB);
                }

                if(adjacentBC != null) {
                    childBC.legalizeEdge(pr, vertices.b, vertices.c, adjacentBC);
                }

                if(adjacentCA != null) {
                    childCA.legalizeEdge(pr, vertices.c, vertices.a, adjacentCA);
                }

                // now internal
                adjacentAB = null;
                adjacentBC = null;
                adjacentCA = null;

                return this;
            } else {
                return childContainingPoint(pr).insertPoint(pr);
            }
        }

        private void legalizeEdge(int pr, int pi, int pj, TriangleNode tn_i_j_k) {
            if(Circumcircle.contains(tn_i_j_k.triangle, mPoints[pr])) {

                // internalize triangles r-i-j and i-j-k
                // create triangles r-i-k and r-j-k
                // call legalize for edges j-k and i-k

                int pk = tn_i_j_k.vertices.getThird(pi, pj);
                TriangleNode tn_r_i_j = tn_i_j_k.adjacentHavingEdge(pi, pj);
                if(tn_r_i_j != this)
                    throw new AssertionError();

                if(!tn_i_j_k.isLeaf() || !tn_r_i_j.isLeaf())
                    throw new AssertionError();

                TriangleNode tn_r_i_k = new TriangleNode(pr, pi, pk);
                TriangleNode tn_r_j_k = new TriangleNode(pr, pj, pk);

                tn_r_i_k.setAdjacent(pr, pk, tn_r_j_k);
                tn_r_i_k.setAdjacent(pr, pi, adjacentHavingEdge(pr, pi));
                tn_r_i_k.setAdjacent(pi, pk, tn_i_j_k.adjacentHavingEdge(pi, pk));

                tn_r_j_k.setAdjacent(pr, pk, tn_r_i_k);
                tn_r_j_k.setAdjacent(pr, pj, adjacentHavingEdge(pr, pj));
                tn_r_j_k.setAdjacent(pj, pk, tn_i_j_k.adjacentHavingEdge(pj, pk));

                TriangleNode tn_i_j_k__k_i = tn_i_j_k.adjacentHavingEdge(pk, pi);
                if(tn_i_j_k__k_i != null)
                    tn_i_j_k__k_i.replaceAdjacent(tn_i_j_k, tn_r_i_k);

                TriangleNode tn_i_j_k__k_j = tn_i_j_k.adjacentHavingEdge(pk, pj);
                if(tn_i_j_k__k_j != null)
                    tn_i_j_k__k_j.replaceAdjacent(tn_i_j_k, tn_r_j_k);

                TriangleNode tn_r_i_j__r_i = tn_r_i_j.adjacentHavingEdge(pr, pi);
                if(tn_r_i_j__r_i != null)
                    tn_r_i_j__r_i.replaceAdjacent(tn_r_i_j, tn_r_i_k);

                TriangleNode tn_r_i_j__r_j = tn_r_i_j.adjacentHavingEdge(pr, pj);
                if(tn_r_i_j__r_j != null)
                    tn_r_i_j__r_j.replaceAdjacent(tn_r_i_j, tn_r_j_k);

                tn_i_j_k.children[0] = tn_r_i_k;
                tn_i_j_k.children[1] = tn_r_j_k;
                if(tn_i_j_k.children[2] != null)
                    throw new AssertionError();

                tn_r_i_j.children[0] = tn_r_i_k;
                tn_r_i_j.children[1] = tn_r_j_k;
                if(tn_r_i_j.children[2] != null)
                    throw new AssertionError();

                // now internal
                tn_r_i_j.adjacentAB = null;
                tn_r_i_j.adjacentBC = null;
                tn_r_i_j.adjacentCA = null;

                // now internal
                tn_i_j_k.adjacentAB = null;
                tn_i_j_k.adjacentBC = null;
                tn_i_j_k.adjacentCA = null;

                if(adjacentHavingEdge(pj, pk) != null)
                    legalizeEdge(pr, pj, pk, adjacentHavingEdge(pj, pk));
                if(adjacentHavingEdge(pi, pk) != null)
                    legalizeEdge(pr, pi, pk, adjacentHavingEdge(pi, pk));
            }
        }

        private void setAdjacent(int pi, int pj, TriangleNode tn) {
            if(pi == vertices.a && pj == vertices.b || pi == vertices.b && pj == vertices.a) {
                if(adjacentAB != null)
                    throw new AssertionError();
                adjacentAB = tn;
                return;
            }
            if(pi == vertices.b && pj == vertices.c || pi == vertices.c && pj == vertices.b) {
                if(adjacentBC != null)
                    throw new AssertionError();
                adjacentBC = tn;
                return;
            }
            if(pi == vertices.c && pj == vertices.a || pi == vertices.a && pj == vertices.c) {
                if(adjacentCA != null)
                    throw new AssertionError();
                adjacentCA = tn;
                return;
            }
            throw new AssertionError();
        }

        private TriangleNode adjacentHavingEdge(int pi, int pj) {
            if(adjacentAB != null && adjacentAB.vertices.includesPair(pi, pj)) {
                return adjacentAB;
            }
            if(adjacentBC != null && adjacentBC.vertices.includesPair(pi, pj)) {
                return adjacentBC;
            }
            if(adjacentCA != null && adjacentCA.vertices.includesPair(pi, pj)) {
                return adjacentCA;
            }
            return null;
        }

        private TriangleNode childContainingPoint(int pi) {
            if(pi < 0) {
                throw new IllegalArgumentException();
            }
            if(isLeaf()) {
                throw new AssertionError();
            }
            for(int childi = 0; childi < 3; childi++) {
                TriangleNode child = children[childi];
                if(child != null && child.contains(pi)) {
                    return child;
                }
            }
            // otherwise, no luck. the point must be on an unlucky boundary or something.
            // todo: expose this case in a test and then deal with it here
            // probably use closest-to-bound
            throw new AssertionError();
        }
    }

    ////////////////////////////////////////////////

    private static final SuperRandom sRandom = new SuperRandom();

    private final Vec2[] mPoints;
    private final TriangleNode mTriangulationRoot;

    public DelaunayTriangulation(Collection<Vec2> points) {
        int n = points.size();
        if(n < 3) {
            throw new IllegalArgumentException();
        }
        mPoints = new Vec2[n + 3];
        int i = 0;
        float max = 0;
        for(Vec2 point : points) {
            mPoints[i++] = point;
            max = Math.max(max, Math.abs(point.x));
            max = Math.max(max, Math.abs(point.y));
        }

        mPoints[n] = new Vec2(9999 * max, 0);
        mPoints[n + 1] = new Vec2(0, 9999 * max);
        mPoints[n + 2] = new Vec2(-9999 * max, -9999 * max);

        sRandom.subShuffle(mPoints, 0, n);

        mTriangulationRoot = new TriangleNode(n, n + 1, n + 2);
        mTriangulationRoot.validateAdjacent();
        for(int j = 0; j < n; j++) {
            mTriangulationRoot.insertPoint(j);
            mTriangulationRoot.validateAdjacent();
            // check and flip
        }
    }

    /*
    private static boolean higherThan(Vec2 lhs, Vec2 rhs) {
        return lhs.y != rhs.y ? lhs.y > rhs.y : lhs.x > rhs.x;
    }

    private static void orderHighestThenRandom(Vec2[] points) {
        int highestIndex = 0;
        Vec2 highest = points[0];
        for(int i = 1; i < points.length; i++) {
            Vec2 p = points[i];
            if(higherThan(p, highest)) {
                highestIndex = i;
                highest = p;
            }
        }
        if(highestIndex != 0) {
            points[highestIndex] = points[0];
            points[0] = highest;
        }
        sRandom.subShuffle(points, 1);
    }
    */

    public ArrayList<Triangle> getTriangles() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        mTriangulationRoot.getLeafTriangles(triangles, mPoints.length - 4);
        return triangles;
    }
}
