package net.chaosworship.topuslib.geom2d.barneshut;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.Collection;


public class BarnesHutTree {

    private BarnesHutNode mRoot;

    public BarnesHutTree(Rectangle area) {
        mRoot = new BarnesHutNode(area.enlargedToSquare());
    }

    public void clear() {
        mRoot.clear();
    }

    public void load(Collection<PointMass> pointMasses) {
        mRoot.clear();
        for(PointMass pointMass : pointMasses) {
            mRoot.insert(pointMass);
        }
    }

    public void getForce(Vec2 position, Vec2 forceAccum) {
        mRoot.getForce(position, forceAccum);
    }
}
