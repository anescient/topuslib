package net.chaosworship.topuslib.geom2d.triangulation;

import net.chaosworship.topuslib.IntPair;
import net.chaosworship.topuslib.IntTriple;
import net.chaosworship.topuslib.geom2d.Triangle;
import net.chaosworship.topuslib.geom2d.Vec2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


@SuppressWarnings("unused")
public class Triangulation {

    private final Vec2[] mPoints;
    private final HashSet<IntTriple> mTriangles;

    public Triangulation(Collection<Vec2> points) {
        mPoints = new Vec2[points.size()];
        int i = 0;
        for(Vec2 p : points) {
            mPoints[i++] = p;
        }
        mTriangles = new HashSet<>();
    }

    public void addTriangle(int i, int j, int k) {
        if(i < 0 || j < 0 || k < 0) {
            throw new IllegalArgumentException();
        }
        if(i >= mPoints.length || j >= mPoints.length || k >= mPoints.length) {
            throw new IllegalArgumentException();
        }
        if(i == j || j == k || i == k) {
            throw new IllegalArgumentException();
        }
        if(!mTriangles.add(IntTriple.sorted(i, j, k))) {
            throw new IllegalArgumentException("already have that triangle");
        }
    }

    public void removeTriangle(int i, int j, int k) {
        if(!mTriangles.remove(IntTriple.sorted(i, j, k))) {
            throw new IllegalStateException("no such triangle");
        }
    }

    public Vec2[] getPoints() {
        return mPoints;
    }

    public HashSet<IntPair> getEdges() {
        HashSet<IntPair> edges = new HashSet<>();
        for(IntTriple triple : mTriangles) {
            edges.add(IntPair.sorted(triple.a, triple.b));
            edges.add(IntPair.sorted(triple.b, triple.c));
            edges.add(IntPair.sorted(triple.c, triple.a));
        }
        return edges;
    }

    public ArrayList<Triangle> resolveTriangles() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        for(IntTriple triple : mTriangles) {
            triangles.add(new Triangle(
                    mPoints[triple.a],
                    mPoints[triple.b],
                    mPoints[triple.c]));
        }
        return triangles;
    }

    public IntPair getOppositePair(IntPair edge) {
        int x1 = -1;
        int x2 = -1;
        for(IntTriple triple : mTriangles) {
            if(triple.includesPair(edge)) {
                int x = triple.getThird(edge);
                if(x1 < 0) {
                    x1 = x;
                } else {
                    x2 = x;
                    break;
                }
            }
        }
        return x1 >= 0 && x2 >= 0 ? new IntPair(x1, x2) : null;
    }
}
