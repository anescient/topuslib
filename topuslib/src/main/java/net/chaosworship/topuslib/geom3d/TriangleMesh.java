package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.collection.TriangleConsumer;
import net.chaosworship.topuslib.tuple.IntTriple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class TriangleMesh {

    private final List<Vec3> mVertices;
    private final List<IntTriple> mFaces;

    @SuppressLint("UseSparseArrays")
    TriangleMesh() {
        mVertices = new ArrayList<>();
        mFaces = new ArrayList<>();
    }

    TriangleMesh(Collection<Vec3> vertices) {
        this();
        mVertices.addAll(vertices);
    }

    TriangleMesh(TriangleMesh source) {
        this(source.getVertices());
        for(IntTriple face : source.getFaces()) {
            addFace(face.a, face.b, face.c);
        }
    }

    public List<Vec3> getVertices() {
        return mVertices;
    }

    public List<IntTriple> getFaces() {
        return mFaces;
    }

    int addVertex(Vec3 position) {
        mVertices.add(position);
        return mVertices.size() - 1;
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
