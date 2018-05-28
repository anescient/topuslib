package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;
import android.util.SparseIntArray;

import net.chaosworship.topuslib.collection.TriangleConsumer;
import net.chaosworship.topuslib.tuple.IntTriple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TriangleMesh {

    private final Map<Integer, Vec3> mVertices;
    private final List<IntTriple> mFaces;
    private int mNextVertex;

    @SuppressLint("UseSparseArrays")
    TriangleMesh() {
        mVertices = new HashMap<>();
        mFaces = new ArrayList<>();
        mNextVertex = 0;
    }

    TriangleMesh(Map<Integer, Vec3> vertices) {
        this();
        for(Map.Entry<Integer, Vec3> entry : vertices.entrySet()) {
            int v = entry.getKey();
            mVertices.put(v, new Vec3(entry.getValue()));
            mNextVertex = Math.max(mNextVertex, v + 1);
        }
    }

    TriangleMesh(TriangleMesh source) {
        this(source.getVertices());
        for(IntTriple face : source.getFaces()) {
            addFace(face.a, face.b, face.c);
        }
    }

    Map<Integer, Vec3> getVertices() {
        return mVertices;
    }

    List<IntTriple> getFaces() {
        return mFaces;
    }

    int addVertex(Vec3 position) {
        mVertices.put(mNextVertex, position);
        return mNextVertex++;
    }

    void addFace(int a, int b, int c) {
        mFaces.add(new IntTriple(a, b, c));
    }

    public void outputTriangles(TriangleConsumer consumer) {
        for(IntTriple face : mFaces) {
            consumer.addTriangle(
                    mVertices.get(face.a),
                    mVertices.get(face.b),
                    mVertices.get(face.c));
        }
    }
}
