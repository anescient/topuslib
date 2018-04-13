package net.chaosworship.topuslib.geom2d.mesh;

import net.chaosworship.topuslib.geom2d.Circumcircle;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.random.SuperRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class DelaunayTriangulator {

    private static final boolean DEBUG_VALIDATEADJACENT = false;

    ////////////////////////////////////////////////

    // thrown if an error is detected during the triangulation process
    // this is usually because of bad luck with floating point
    public static class NumericalFailure extends Exception {}

    ////////////////////////////////////////////////

    private class TriangleNode {

        private int vertexA;
        private int vertexB;
        private int vertexC;
        private Triangle triangle;
        private final TriangleNode[] children;
        private TriangleNode adjacentAB; // node with triangle sharing edge AB
        private TriangleNode adjacentBC;
        private TriangleNode adjacentCA;
        private Vec2 tripleSplit; // point used to split this triangle to 3 children
        private Vec2 flipSplitA; // points used to flip an edge and divide this triangle
        private Vec2 flipSplitB;

        // do not descend this when iterating leaf triangles
        // some nodes have multiple parents
        private boolean breakLeafIteration;

        private TriangleNode() {
            triangle = null;
            children = new TriangleNode[3];
        }

        private TriangleNode set(int a, int b, int c) {
            vertexA = a;
            vertexB = b;
            vertexC = c;
            if(triangle == null) {
                triangle = new Triangle(mPoints[vertexA], mPoints[vertexB], mPoints[vertexC]);
            } else {
                triangle.set(mPoints[vertexA], mPoints[vertexB], mPoints[vertexC]);
            }
            children[0] = null;
            children[1] = null;
            children[2] = null;
            adjacentAB = null;
            adjacentBC = null;
            adjacentCA = null;
            tripleSplit = null;
            flipSplitA = null;
            flipSplitB = null;
            breakLeafIteration = false;
            return this;
        }

        private void validateAdjacent() {
            validateAdjacent(new ArrayList<TriangleNode>());
        }

        private void validateAdjacent(ArrayList<TriangleNode> visited) {
            if(adjacentAB != null) {
                if(adjacentAB == adjacentBC || adjacentAB == adjacentCA)
                    throw new AssertionError();
                if(adjacentHavingEdge(vertexA, vertexB) != adjacentAB)
                    throw new AssertionError();
                if(adjacentAB.adjacentHavingEdge(vertexA, vertexB) != this)
                    throw new AssertionError();
            }
            if(adjacentBC != null) {
                if(adjacentBC == adjacentCA)
                    throw new AssertionError();
                if(adjacentHavingEdge(vertexB, vertexC) != adjacentBC)
                    throw new AssertionError();
                if(adjacentBC.adjacentHavingEdge(vertexB, vertexC) != this)
                    throw new AssertionError();
            }
            if(adjacentCA != null) {
                if(adjacentHavingEdge(vertexA, vertexC) != adjacentCA)
                    throw new AssertionError();
                if(adjacentCA.adjacentHavingEdge(vertexA, vertexC) != this)
                    throw new AssertionError();
            }
            visited.add(this);
            for(TriangleNode child : children) {
                if(child != null && !visited.contains(child))
                    child.validateAdjacent(visited);
            }
        }

        private boolean isLeaf() {
            return children[0] == null; // implies other children are null, too
        }

        private boolean contains(int pi) {
            return triangle.contains(mPoints[pi]);
        }

        private void getLeafTriangles(Collection<Triangle> triangles, int maxVertex) {
            if(breakLeafIteration)
                return;
            if(isLeaf()) {
                if(vertexA <= maxVertex && vertexB <= maxVertex && vertexC <= maxVertex) {
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

        private void outputTriangulation(Triangulation triangulation, int maxVertex) {
            if(breakLeafIteration)
                return;
            if(isLeaf()) {
                if(vertexA < vertexB && vertexB <= maxVertex) {
                    triangulation.addEdge(vertexA, vertexB);
                }
                if(vertexC < vertexA && vertexA <= maxVertex) {
                    triangulation.addEdge(vertexC, vertexA);
                }
                if(vertexB < vertexC && vertexC <= maxVertex) {
                    triangulation.addEdge(vertexB, vertexC);
                }
                if(vertexA <= maxVertex && vertexB <= maxVertex && vertexC <= maxVertex) {
                    triangulation.addTriangle(triangle);
                }
            } else {
                for(TriangleNode child : children) {
                    if(child == null) {
                        break;
                    }
                    child.outputTriangulation(triangulation, maxVertex);
                }
            }
        }

        private void appendEdgeGraph(SimpleGraph graph, int maxVertex) {
            if(breakLeafIteration)
                return;
            if(isLeaf()) {
                boolean includeA = vertexA <= maxVertex;
                boolean includeB = vertexB <= maxVertex;
                boolean includeC = vertexC <= maxVertex;
                if(includeA && includeB) {
                    graph.tryAddEdge(vertexA, vertexB);
                }
                if(includeA && includeC) {
                    graph.tryAddEdge(vertexA, vertexC);
                }
                if(includeB && includeC) {
                    graph.tryAddEdge(vertexB, vertexC);
                }
            } else {
                for(int childi = 0; childi < 3; childi++) {
                    TriangleNode child = children[childi];
                    if(child != null) {
                        child.appendEdgeGraph(graph, maxVertex);
                    }
                }
            }
        }

        private void replaceAdjacent(TriangleNode was, TriangleNode is) throws NumericalFailure {
            // assert was != is
            if(adjacentAB == was) {
                adjacentAB = is;
            } else if(adjacentBC == was) {
                adjacentBC = is;
            } else if(adjacentCA == was) {
                adjacentCA = is;
            } else {
                throw new NumericalFailure();
            }
        }

        private void insertPoint(int pr) throws NumericalFailure {

            /*
            Vec2 p = mPoints[pr];
            if(p.inHalfPlane(triangle.pointA, triangle.pointB))
                throw new AssertionError();
            if(p.inHalfPlane(triangle.pointB, triangle.pointC))
                throw new AssertionError();
            if(p.inHalfPlane(triangle.pointC, triangle.pointA))
                throw new AssertionError();
            */

            if(isLeaf()) {
                TriangleNode childAB = children[0] = getTriangleNode().set(vertexA, vertexB, pr);
                TriangleNode childBC = children[1] = getTriangleNode().set(pr, vertexB, vertexC);
                TriangleNode childCA = children[2] = getTriangleNode().set(vertexA, pr, vertexC);

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

                // now internal
                adjacentAB = null;
                adjacentBC = null;
                adjacentCA = null;

                tripleSplit = mPoints[pr];

                if(childAB.adjacentAB != null) {
                    childAB.legalizeEdge(pr, vertexA, vertexB, childAB.adjacentAB);
                }

                if(childBC.adjacentBC != null) {
                    childBC.legalizeEdge(pr, vertexB, vertexC, childBC.adjacentBC);
                }

                if(childCA.adjacentCA != null) {
                    childCA.legalizeEdge(pr, vertexC, vertexA, childCA.adjacentCA);
                }

            } else {
                childContainingPoint(pr).insertPoint(pr);
            }
        }

        private int getThirdVertex(int a, int b) {
            // assert includes vertices a and b
            // assert vertices distinct
            if(vertexA == a) {
                return vertexB == b ? vertexC : vertexB;
            } else if(vertexB == a) {
                return vertexA == b ? vertexC : vertexA;
            } else if(vertexC == a) {
                return vertexA == b ? vertexB : vertexA;
            } else {
                throw new AssertionError();
            }
        }

        private void legalizeEdge(int pr, int pi, int pj, TriangleNode tn_i_j_k) throws NumericalFailure {
            if(Circumcircle.contains(tn_i_j_k.triangle, mPoints[pr])) {

                // internalize triangles r-i-j and i-j-k
                // create triangles i-k-r and r-k-j
                // call legalize for edges j-k and i-k

                int pk = tn_i_j_k.getThirdVertex(pi, pj);
                TriangleNode tn_r_j_i = this;
                // assert tn_r_j_i = tn_i_j_k.adjacentHavingEdge(pi, pj);

                // assert not tn_i_j_k.isLeaf()
                // assert not tn_r_j_i.isLeaf()

                tn_r_j_i.flipSplitA = mPoints[pk];
                tn_r_j_i.flipSplitB = mPoints[pr];

                TriangleNode tn_i_k_r = getTriangleNode().set(pi, pk, pr);
                TriangleNode tn_r_k_j = getTriangleNode().set(pr, pk, pj);

                TriangleNode tn_i_j_k__j_k = tn_i_j_k.adjacentHavingEdge(pj, pk);
                TriangleNode tn_i_j_k__k_i = tn_i_j_k.adjacentHavingEdge(pk, pi);
                TriangleNode tn_r_j_i__r_i = tn_r_j_i.adjacentHavingEdge(pr, pi);
                TriangleNode tn_r_j_i__r_j = tn_r_j_i.adjacentHavingEdge(pr, pj);

                tn_i_k_r.adjacentBC = tn_r_k_j;
                tn_i_k_r.adjacentCA = tn_r_j_i__r_i;
                tn_i_k_r.adjacentAB = tn_i_j_k__k_i;

                tn_r_k_j.adjacentAB = tn_i_k_r;
                tn_r_k_j.adjacentCA = tn_r_j_i__r_j;
                tn_r_k_j.adjacentBC = tn_i_j_k__j_k;

                if(tn_i_j_k__k_i != null)
                    tn_i_j_k__k_i.replaceAdjacent(tn_i_j_k, tn_i_k_r);

                if(tn_i_j_k__j_k != null)
                    tn_i_j_k__j_k.replaceAdjacent(tn_i_j_k, tn_r_k_j);

                if(tn_r_j_i__r_i != null)
                    tn_r_j_i__r_i.replaceAdjacent(tn_r_j_i, tn_i_k_r);

                if(tn_r_j_i__r_j != null)
                    tn_r_j_i__r_j.replaceAdjacent(tn_r_j_i, tn_r_k_j);

                // now internal
                tn_r_j_i.adjacentAB = null;
                tn_r_j_i.adjacentBC = null;
                tn_r_j_i.adjacentCA = null;
                tn_r_j_i.children[0] = tn_i_k_r;
                tn_r_j_i.children[1] = tn_r_k_j;
                // assert tn_r_j_i.children[2] == null

                // now internal
                tn_i_j_k.adjacentAB = null;
                tn_i_j_k.adjacentBC = null;
                tn_i_j_k.adjacentCA = null;
                tn_i_j_k.children[0] = tn_i_k_r;
                tn_i_j_k.children[1] = tn_r_k_j;
                // assert tn_i_j_k.children[2] == null
                tn_i_j_k.breakLeafIteration = true;

                if(tn_i_j_k__j_k != null)
                    tn_r_k_j.legalizeEdge(pr, pk, pj, tn_i_j_k__j_k);

                if(tn_i_j_k__k_i != null)
                    tn_i_k_r.legalizeEdge(pr, pi, pk, tn_i_j_k__k_i);
            }
        }

        private boolean includesVertices(int a, int b) {
            return (vertexA == a || vertexB == a || vertexC == a) &&
                    (vertexA == b || vertexB == b || vertexC == b);
        }

        private TriangleNode adjacentHavingEdge(int pi, int pj) {
            if(adjacentAB != null && adjacentAB.includesVertices(pi, pj)) {
                return adjacentAB;
            }
            if(adjacentBC != null && adjacentBC.includesVertices(pi, pj)) {
                return adjacentBC;
            }
            if(adjacentCA != null && adjacentCA.includesVertices(pi, pj)) {
                return adjacentCA;
            }
            return null;
        }

        private TriangleNode childContainingPoint(int pi) {
            if(pi < 0) {
                throw new IllegalArgumentException();
            }

            // assert not a leaf

            // these half-plane tests are done forward and backward
            // because points sometimes fall on open boundary, in neither half-plane
            // i.e. !inHalfPlane(a,b) does not imply inHalfPlane(b,a)

            if(tripleSplit != null) {
                Vec2 p = mPoints[pi];
                if(p.inHalfPlane(triangle.pointA, tripleSplit)) {
                    if(p.inHalfPlane(triangle.pointB, tripleSplit)) {
                        return children[1];
                    } else if(p.inHalfPlane(tripleSplit, triangle.pointB)) {
                        return children[0];
                    }
                } else if(p.inHalfPlane(tripleSplit, triangle.pointA)) {
                    if(p.inHalfPlane(triangle.pointC, tripleSplit)) {
                        return children[2];
                    } else if(p.inHalfPlane(tripleSplit, triangle.pointC)) {
                        return children[1];
                    }
                }
            }

            if(flipSplitA != null) {
                if(flipSplitB == null || children[2] != null)
                    throw new AssertionError();
                Vec2 p = mPoints[pi];
                if(p.inHalfPlane(flipSplitA, flipSplitB)) {
                    return children[1];
                }
                if(p.inHalfPlane(flipSplitB, flipSplitA)) {
                    return children[0];
                }
            }

            for(int childi = 0; childi < 3; childi++) {
                TriangleNode child = children[childi];
                if(child != null && child.contains(pi)) {
                    return child;
                }
            }

            // otherwise, no luck. the point must be on an unlucky boundary or something.
            // todo: expose this case in a test and then deal with it here
            // for now just use closest-to-bound
            Vec2 p = mPoints[pi];
            int closesti = 0;
            float closestDist = children[0].triangle.distanceSquaredFromBound(p);
            for(int childi = 1; childi < 3; childi++) {
                if(children[childi] == null)
                    break;
                float dist = children[childi].triangle.distanceSquaredFromBound(p);
                if(dist < closestDist) {
                    closestDist = dist;
                    closesti = childi;
                }
            }
            return children[closesti];
        }
    }

    ////////////////////////////////////////////////

    private static final SuperRandom sRandom = new SuperRandom();

    private Vec2[] mPoints;
    private int[] mPointsInsertOrder;
    private TriangleNode mTriangulationRoot;
    private Triangulation mTriangulation;

    private TriangleNode[] mNodePool;
    private int mNextNodeFromPool;

    public DelaunayTriangulator() {
        mPoints = new Vec2[0];
        mPointsInsertOrder = new int[0];
        mTriangulation = new Triangulation();
        mNodePool = new TriangleNode[0];
        mNextNodeFromPool = 0;
    }

    private TriangleNode getTriangleNode() {
        if(mNextNodeFromPool >= mNodePool.length) {
            growNodePool(mNodePool.length * 4 / 3);
        }
        return mNodePool[mNextNodeFromPool++];
    }

    private void growNodePool(int size) {
        if(size <= mNodePool.length) {
            return;
        }
        TriangleNode[] newPool = Arrays.copyOf(mNodePool, size);
        for(int i = mNodePool.length; i < newPool.length; i++) {
            newPool[i] = new TriangleNode();
        }
        mNodePool = newPool;
    }

    // todo maybe retriangulate, using same Vec2 objects with new values
    public Triangulation triangulate(Collection<Vec2> points) throws NumericalFailure {
        int n = points.size();
        if(n < 3) {
            throw new IllegalArgumentException();
        }

        // uniform random points uses approx. 7 nodes per point
        growNodePool(8 * points.size());
        mNextNodeFromPool = 0;

        if(mPoints.length != n + 3) {
            mPoints = new Vec2[n + 3];
        }
        int i = 0;
        float max = 0;
        for(Vec2 point : points) {
            mPoints[i++] = point;
            max = Math.max(max, Math.abs(point.x));
            max = Math.max(max, Math.abs(point.y));
        }

        // todo: make the bounds symbolic and infinitely distant
        float boundScale = 9999;
        mPoints[n] = new Vec2(boundScale * max, 0);
        mPoints[n + 1] = new Vec2(0, boundScale * max);
        mPoints[n + 2] = new Vec2(-boundScale * max, -boundScale * max);

        if(mPointsInsertOrder.length != n) {
            mPointsInsertOrder = new int[n];
            for(i = 0; i < n; i++) {
                mPointsInsertOrder[i] = i;
            }
            sRandom.shuffle(mPointsInsertOrder);
        }

        mTriangulationRoot = getTriangleNode().set(n, n + 1, n + 2);
        if(DEBUG_VALIDATEADJACENT)
            mTriangulationRoot.validateAdjacent();
        for(int orderi : mPointsInsertOrder) {
            int pi = mPointsInsertOrder[orderi];
            mTriangulationRoot.insertPoint(pi);
            if(DEBUG_VALIDATEADJACENT)
                mTriangulationRoot.validateAdjacent();
        }

        mTriangulation.init(mPoints);
        mTriangulationRoot.outputTriangulation(mTriangulation, mPoints.length - 4);
        return mTriangulation;
    }

    public ArrayList<Triangle> getTriangles() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        if(mTriangulationRoot != null) {
            mTriangulationRoot.getLeafTriangles(triangles, mPoints.length - 4);
        }
        return triangles;
    }

    public void getEdgeGraph(SimpleGraph graph) {
        graph.clear();
        if(mTriangulationRoot != null) {
            for(int i = 0; i <= mPoints.length - 4; i++) {
                graph.addVertex(i);
            }
            mTriangulationRoot.appendEdgeGraph(graph, mPoints.length - 4);
        }
    }
}
