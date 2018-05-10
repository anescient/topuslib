package net.chaosworship.topuslib.geom2d.barneshut;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;

import java.util.ArrayList;


class BarnesHutNode {

    private final Rectangle mArea;
    private final float mSplitX;
    private final float mSplitY;
    private final ArrayList<PointMass> mPointMasses;
    private PointMass mSumPointMass;
    private BarnesHutNode mChildLessXLessY;
    private BarnesHutNode mChildLessXMoreY;
    private BarnesHutNode mChildMoreXLessY;
    private BarnesHutNode mChildMoreXMoreY;

    BarnesHutNode(Rectangle area) {
        mArea = area;
        mSplitX = mArea.centerX();
        mSplitY = mArea.centerY();
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
        } else {
            if(mPointMasses.size() == 1) {
                createChildren();
                putToChild(mPointMasses.get(0));
            }
            mPointMasses.add(pointMass);
            putToChild(pointMass);
        }
    }

    private void putToChild(PointMass pointMass) {
        if(pointMass.position.x <= mSplitX) {
            if(pointMass.position.y <= mSplitY) {
                mChildLessXLessY.insert(pointMass);
            } else { // y > mSplitY
                mChildLessXMoreY.insert(pointMass);
            }
        } else { // x > mSplitX
            if(pointMass.position.y <= mSplitY) {
                mChildMoreXLessY.insert(pointMass);
            } else { // y > mSplitY
                mChildMoreXMoreY.insert(pointMass);
            }
        }
    }

    private void createChildren() {
        if(BuildConfig.DEBUG) {
            if(mChildLessXLessY != null ||
                    mChildLessXMoreY != null ||
                    mChildMoreXLessY != null ||
                    mChildMoreXMoreY != null) {
                throw new AssertionError();
            }
        }

        Rectangle childRect;

        childRect = new Rectangle(mArea.minx, mArea.miny, mSplitX, mSplitY);
        mChildLessXLessY = new BarnesHutNode(childRect);

        childRect = new Rectangle(mArea.minx, mSplitY, mSplitX, mArea.maxy);
        mChildLessXMoreY = new BarnesHutNode(childRect);

        childRect = new Rectangle(mSplitX, mArea.miny, mArea.maxx, mSplitY);
        mChildMoreXLessY = new BarnesHutNode(childRect);

        childRect = new Rectangle(mSplitX, mSplitY, mArea.maxx, mArea.maxy);
        mChildMoreXMoreY = new BarnesHutNode(childRect);
    }

    Vec2 getForce(Vec2 position) {
        if(mPointMasses.isEmpty()) {
            return new Vec2(0, 0);
        }
        boolean open = false;
        open |= mArea.contains(position);
        if(!open) {
            float diameter = mArea.width() * 1.4142f;
            float distance = Vec2.distance(position, mArea.center());
            if(distance < 2 * diameter) {
                open = true;
            }
        }
        Vec2 force = new Vec2(0, 0);
        if(open && mPointMasses.size() > 1) {
            force.add(mChildLessXLessY.getForce(position));
            force.add(mChildLessXMoreY.getForce(position));
            force.add(mChildMoreXLessY.getForce(position));
            force.add(mChildMoreXMoreY.getForce(position));
        } else {
            if(mSumPointMass == null) {
                mSumPointMass = new PointMass();
                for(PointMass pointMass : mPointMasses) {
                    mSumPointMass.position.addScaled(pointMass.position, pointMass.mass);
                    mSumPointMass.mass += pointMass.mass;
                }
                mSumPointMass.position.scaleInverse(mSumPointMass.mass);
            }
            float distance = Vec2.distance(position, mSumPointMass.position);
            if(distance > 0) {
                force.setDifference(position, mSumPointMass.position)
                        .normalize().scaleInverse(distance * distance);
            }
        }
        return force;
    }
}
