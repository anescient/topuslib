package net.chaosworship.topuslib.geom2d.barneshut;

import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.ArrayList;


class BarnesHutNode {

    private final ArrayList<PointMass> mPointMasses;
    private PointMass mSumPointMass;
    BarnesHutNode mChildLessXLessY;
    BarnesHutNode mChildLessXMoreY;
    BarnesHutNode mChildMoreXLessY;
    BarnesHutNode mChildMoreXMoreY;


    BarnesHutNode(Rectangle area) {
        mPointMasses = new ArrayList<>();
        mSumPointMass = null;
        mChildLessXLessY = null;
        mChildLessXMoreY = null;
        mChildMoreXLessY = null;
        mChildMoreXMoreY = null;
    }

    void insert(PointMass pointMass) {
        mSumPointMass = null;
        if(mPointMasses.isEmpty()) {
            mPointMasses.add(pointMass);
        }
    }
}
