package net.chaosworship.topuslib.geom2d.barneshut;

import android.util.Log;

import net.chaosworship.topuslib.BuildConfig;
import net.chaosworship.topuslib.geom2d.Rectangle;
import net.chaosworship.topuslib.geom2d.Vec2;
import net.chaosworship.topuslib.tuple.PointMass;


class BarnesHutNode {

    private static final float OPENINGRATIO = 1.0f;

    private static int nodecount = 0;

    private final Rectangle mArea;
    private final float mSplitX;
    private final float mSplitY;
    private PointMass mSumPointMass;
    private int mPointMassCount;
    private BarnesHutNode mChildLessXLessY;
    private BarnesHutNode mChildLessXMoreY;
    private BarnesHutNode mChildMoreXLessY;
    private BarnesHutNode mChildMoreXMoreY;

    private final Vec2 mTempDiff;

    BarnesHutNode(Rectangle area) {
        nodecount++;
        if(nodecount % 100 == 0)
            Log.d("bht", String.valueOf(nodecount));

        mArea = area;
        mSplitX = mArea.centerX();
        mSplitY = mArea.centerY();
        mSumPointMass = new PointMass();
        mPointMassCount = 0;
        mChildLessXLessY = null;
        mChildLessXMoreY = null;
        mChildMoreXLessY = null;
        mChildMoreXMoreY = null;
        mTempDiff = new Vec2();
    }

    void clear() {
        mSumPointMass.position.setZero();
        mSumPointMass.mass = 0;
        mPointMassCount = 0;
    }

    void insert(PointMass pointMass) {
        if(mPointMassCount == 0) {
            if(mChildLessXLessY != null) {
                mChildLessXLessY.clear();
                mChildLessXMoreY.clear();
                mChildMoreXLessY.clear();
                mChildMoreXMoreY.clear();
            }
        }

        mSumPointMass.position.addScaled(pointMass.position, pointMass.mass);
        mSumPointMass.mass += pointMass.mass;
        mPointMassCount++;
        if(mChildLessXLessY == null) {
            createChildren();
        }
        if(mPointMassCount > 1) {
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

    void getForce(Vec2 position, Vec2 forceAccum) {
        if(mPointMassCount == 0) {
            return;
        }
        if(mPointMassCount > 1) {
            mSumPointMass.position.scaleInverse(mSumPointMass.mass);
            mPointMassCount = 1;
        }
        boolean open = mArea.contains(position);
        if(!open) {
            float diameter = mArea.width() * 1.4142f; // diagonal of a square
            float distance = Vec2.distance(position, mSumPointMass.position);
            if(distance < OPENINGRATIO * diameter) {
                open = true;
            }
        }
        if(open) {
            mChildLessXLessY.getForce(position, forceAccum);
            mChildLessXMoreY.getForce(position, forceAccum);
            mChildMoreXLessY.getForce(position, forceAccum);
            mChildMoreXMoreY.getForce(position, forceAccum);
        } else {
            mTempDiff.setDifference(position, mSumPointMass.position);
            float distance = mTempDiff.magnitude();
            if(distance > 0) {
                forceAccum.addScaled(mTempDiff, mSumPointMass.mass / (distance * distance * distance));
            }
        }
    }
}
