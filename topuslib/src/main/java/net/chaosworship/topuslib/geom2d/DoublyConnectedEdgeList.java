package net.chaosworship.topuslib.geom2d;

import java.util.ArrayList;


public class DoublyConnectedEdgeList<TVertex, TEdge, TFace> {

    ////////////////////////////////////////////

    public class Vertex {
        Vec2 position;
        HalfEdge incidentEdge;
        TVertex data;

        private Vertex(Vec2 position, TVertex data) {
            this.position = position;
            this.incidentEdge = null;
            this.data = data;
        }
    }

    public class HalfEdge {
        Vertex origin;
        HalfEdge twin;
        HalfEdge prev;
        HalfEdge next;
        Face incidentFace;
        TEdge data;

        private HalfEdge(Vertex origin, TEdge data) {
            this.origin = origin;
            this.twin = null;
            this.prev = null;
            this.next = null;
            this.incidentFace = null;
            this.data = data;
        }
    }

    public class Face {
        HalfEdge outerEdge;
        TFace data;

        private Face(HalfEdge outerEdge, TFace data) {
            this.outerEdge = outerEdge;
            this.data = data;
        }
    }

    ////////////////////////////////////////////

    private final ArrayList<Vertex> mVertices;
    private final ArrayList<HalfEdge> mHalfEdges;
    private final ArrayList<Face> mFaces;

    public DoublyConnectedEdgeList() {
        mVertices = new ArrayList<>();
        mHalfEdges = new ArrayList<>();
        mFaces = new ArrayList<>();
    }

    public Vertex addVertex(Vec2 position, TVertex data) {
        Vertex v = new Vertex(position, data);
        mVertices.add(v);
        return v;
    }

    public HalfEdge addHalfEdge(Vertex origin, TEdge data) {
        HalfEdge he = new HalfEdge(origin, data);
        mHalfEdges.add(he);
        if(origin.incidentEdge == null) {
            origin.incidentEdge = he;
        }
        return he;
    }

    public HalfEdge addHalfEdge(Vertex origin, HalfEdge twin, TEdge data) {
        if(twin == null) {
            throw new IllegalArgumentException();
        }
        if(twin.twin != null) {
            throw new IllegalArgumentException("twin already has a twin");
        }
        HalfEdge he = new HalfEdge(origin, data);
        he.twin = twin;
        twin.twin = he;
        return he;
    }

    public Face addFace(HalfEdge edge, TFace data) {
        Face f = new Face(edge, data);
        mFaces.add(f);
        return f;
    }
}
