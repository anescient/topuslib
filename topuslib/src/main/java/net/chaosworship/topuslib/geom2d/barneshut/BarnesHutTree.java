package net.chaosworship.topuslib.geom2d.barneshut;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.Collection;


public class BarnesHutTree {

    private final Rectangle mArea;
    private BarnesHutNode mRoot;


    public BarnesHutTree(Rectangle area) {
        mArea = area.enlargedToSquare();
        mRoot = null;
    }

    public void clear() {
        mRoot = null;
    }

    public void load(Collection<PointMass> pointMasses) {
        if(mRoot == null) {
            mRoot = new BarnesHutNode(mArea);
        }
        for(PointMass pointMass : pointMasses) {
            mRoot.insert(pointMass);
        }
    }

    public Vec2 getForce(Vec2 position) {
        return mRoot.getForce(position);
    }
}
