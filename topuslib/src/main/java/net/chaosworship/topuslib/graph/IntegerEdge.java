package net.chaosworship.topuslib.graph;


import net.chaosworship.topuslib.geom2d.Vec2;

public class IntegerEdge {

    public final int a;
    public final int b;

    public IntegerEdge(int a, int b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs == null || !this.getClass().equals(rhs.getClass())) {
            return false;
        }

        IntegerEdge e = (IntegerEdge) rhs;
        return a == e.a && b == e.b;
    }

    @Override
    public int hashCode() {
        return a + 137 * b;
    }


}
