package net.chaosworship.topuslib.geom2d.barneshut;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.Collection;


public class BarnesHutTree {

    private BarnesHutNode mRoot;


    public BarnesHutTree() {
        mRoot = null;
    }

    public void clear() {
        mRoot = null;
    }

    public void load(Collection<PointMass> pointMasses) {
        //Rectangle bound = Rectangle.squareBound(pointMasses);

    }

    public Vec2 getForce(Vec2 position) {
        return null;
    }
}
