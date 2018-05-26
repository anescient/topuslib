package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.gl.GLLinesBrush;
import net.chaosworship.topuslib.graph.HashSimpleGraph;
import net.chaosworship.topuslib.graph.SimpleGraph;
import net.chaosworship.topuslib.tuple.IntTriple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Icosahedron {

    private final Map<Integer, Vec3> mVertices;
    private final ArrayList<IntTriple> mFaces;
    private final SimpleGraph mEdgeGraph;

    @SuppressLint("UseSparseArrays")
    public Icosahedron() {
        mVertices = new HashMap<>();
        mFaces = new ArrayList<>();
        mEdgeGraph = new HashSimpleGraph();

        final float d = (float)((1 + Math.sqrt(5)) / 2);

        mVertices.put(0, new Vec3(-1, d, 0));
        mVertices.put(1, new Vec3(1, d, 0));
        mVertices.put(2, new Vec3(-1, -d, 0));
        mVertices.put(3, new Vec3(1, -d, 0));

        mVertices.put(4, new Vec3(0, -1, d));
        mVertices.put(5, new Vec3(0, 1, d));
        mVertices.put(6, new Vec3(0, -1, -d));
        mVertices.put(7, new Vec3(0, 1, -d));

        mVertices.put(8, new Vec3(d, 0, -1));
        mVertices.put(9, new Vec3(d, 0, 1));
        mVertices.put(10, new Vec3(-d, 0, -1));
        mVertices.put(11, new Vec3(-d, 0, 1));

        final float normalize = 1.0f / new Vec3(1, d, 0).magnitude();
        for(Vec3 v : mVertices.values()) {
            v.scale(normalize);
        }

        mFaces.add(new IntTriple(0, 11, 5));
        mFaces.add(new IntTriple(0, 5, 1));
        mFaces.add(new IntTriple(0, 1, 7));
        mFaces.add(new IntTriple(0, 7, 10));
        mFaces.add(new IntTriple(0, 10, 11));

        mFaces.add(new IntTriple(1, 5, 9));
        mFaces.add(new IntTriple(5, 11, 4));
        mFaces.add(new IntTriple(11, 10, 2));
        mFaces.add(new IntTriple(10, 7, 6));
        mFaces.add(new IntTriple(7, 1, 8));

        mFaces.add(new IntTriple(3, 9, 4));
        mFaces.add(new IntTriple(3, 4, 2));
        mFaces.add(new IntTriple(3, 2, 6));
        mFaces.add(new IntTriple(3, 6, 8));
        mFaces.add(new IntTriple(3, 8, 9));

        mFaces.add(new IntTriple(4, 9, 5));
        mFaces.add(new IntTriple(2, 4, 11));
        mFaces.add(new IntTriple(6, 2, 10));
        mFaces.add(new IntTriple(8, 6, 7));
        mFaces.add(new IntTriple(9, 8, 1));

        for(int v = 0; v < 12; v++) {
            mEdgeGraph.addVertex(v);
        }
        for(IntTriple triple : mFaces) {
            mEdgeGraph.tryAddEdge(triple.a, triple.b);
            mEdgeGraph.tryAddEdge(triple.b, triple.c);
            mEdgeGraph.tryAddEdge(triple.c, triple.a);
        }
    }

    public void drawEdges(GLLinesBrush linesBrush) {
        linesBrush.addGraph(mEdgeGraph, mVertices);
    }
}
