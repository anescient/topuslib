package net.chaosworship.topuslib.geom3d;

import android.annotation.SuppressLint;

import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.IntPair;
import net.chaosworship.topuslib.tuple.IntTriple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressLint("UseSparseArrays")
public class TriangulatedSphere {

    private TriangulatedSphere() {}

    @SuppressWarnings({"WeakerAccess", "SuspiciousNameCombination"})
    public static TriangleMesh generateIcosahedron() {
        TriangleMesh mesh = new TriangleMesh();

        Vec2 v = new Vec2(1, (float)((1 + Math.sqrt(5)) / 2)).normalize();

        int a = mesh.addVertex(new Vec3(-v.x, v.y, 0));
        int b = mesh.addVertex(new Vec3(v.x, v.y, 0));
        int c = mesh.addVertex(new Vec3(-v.x, -v.y, 0));
        int d = mesh.addVertex(new Vec3(v.x, -v.y, 0));

        int e = mesh.addVertex(new Vec3(0, -v.x, v.y));
        int f = mesh.addVertex(new Vec3(0, v.x, v.y));
        int g = mesh.addVertex(new Vec3(0, -v.x, -v.y));
        int h = mesh.addVertex(new Vec3(0, v.x, -v.y));

        int i = mesh.addVertex(new Vec3(v.y, 0, -v.x));
        int j = mesh.addVertex(new Vec3(v.y, 0, v.x));
        int k = mesh.addVertex(new Vec3(-v.y, 0, -v.x));
        int l = mesh.addVertex(new Vec3(-v.y, 0, v.x));

        mesh.addFace(a, l, f);
        mesh.addFace(a, f, b);
        mesh.addFace(a, b, h);
        mesh.addFace(a, h, k);
        mesh.addFace(a, k, l);

        mesh.addFace(b, f, j);
        mesh.addFace(f, l, e);
        mesh.addFace(l, k, c);
        mesh.addFace(k, h, g);
        mesh.addFace(h, b, i);

        mesh.addFace(d, j, e);
        mesh.addFace(d, e, c);
        mesh.addFace(d, c, g);
        mesh.addFace(d, g, i);
        mesh.addFace(d, i, j);

        mesh.addFace(e, j, f);
        mesh.addFace(c, e, l);
        mesh.addFace(g, c, k);
        mesh.addFace(i, g, h);
        mesh.addFace(j, i, b);

        return mesh;
    }

    public static TriangleMesh generateIcosphere(int subdivisionLevel) {
        if(subdivisionLevel < 1) {
            throw new IllegalArgumentException();
        }
        TriangleMesh icosphere = generateIcosahedron();
        while(subdivisionLevel-- > 1) {
            icosphere = subdivide(icosphere);
        }
        return icosphere;
    }

    private static TriangleMesh subdivide(TriangleMesh base) {
        TriangleMesh submesh = new TriangleMesh(base.getVertices());
        Map<Integer, Vec3> vertices = submesh.getVertices();
        ArrayList<IntPair> edges = new ArrayList<>();
        HashMap<IntPair, Integer> midpointMap = new HashMap<>();
        for(IntTriple face : base.getFaces()) {
            if(face.a < face.b) { edges.add(new IntPair(face.a, face.b)); }
            if(face.b < face.c) { edges.add(new IntPair(face.b, face.c)); }
            if(face.c < face.a) { edges.add(new IntPair(face.c, face.a)); }
        }
        for(IntPair edge : edges) {
            Vec3 ab = Vec3.midpoint(vertices.get(edge.a), vertices.get(edge.b)).normalize();
            midpointMap.put(edge, submesh.addVertex(ab));
        }
        for(IntTriple face : base.getFaces()) {
            int ab = midpointMap.get(IntPair.sorted(face.a, face.b));
            int bc = midpointMap.get(IntPair.sorted(face.b, face.c));
            int ca = midpointMap.get(IntPair.sorted(face.c, face.a));
            submesh.addFace(face.a, ab, ca);
            submesh.addFace(face.b, bc, ab);
            submesh.addFace(face.c, ca, bc);
            submesh.addFace(ab, bc, ca);
        }
        return submesh;
    }
}
