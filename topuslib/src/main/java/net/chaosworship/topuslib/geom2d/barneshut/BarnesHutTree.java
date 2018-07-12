package net.chaosworship.topuslib.geom2d.barneshut;

import android.util.Log;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.Collection;


public class BarnesHutTree {

    private BarnesHutNode mRoot;

    // openingRatio
    // 0.5 is pretty good accuracy
    // 0.2 is probably fine
    // less is fast, more is accurate
    public BarnesHutTree(Rectangle area, float openingRatio) {
        mRoot = new BarnesHutNode(area.enlargedToSquare(), openingRatio);
    }

    public void clear() {
        mRoot.clear();
    }

    public void load(Collection<PointMass> pointMasses) {
        load(pointMasses, 0);
    }

    public void load(Collection<PointMass> pointMasses, float minimumMass) {
        mRoot.clear();
        for(PointMass pointMass : pointMasses) {
            if(pointMass.mass >= minimumMass) {
                mRoot.insert(pointMass);
            }
        }
        //Log.d("bht size", String.valueOf(mRoot.treeSize()));
    }

    public void getForce(Vec2 position, Vec2 forceAccum, float minDistance) {
        mRoot.getForce(position, forceAccum, minDistance);
    }
}
